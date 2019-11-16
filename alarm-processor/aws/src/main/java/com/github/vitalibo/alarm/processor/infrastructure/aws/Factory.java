package com.github.vitalibo.alarm.processor.infrastructure.aws;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClient;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.github.vitalibo.alarm.processor.core.Spark;
import com.github.vitalibo.alarm.processor.core.Stream;
import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.model.Metric;
import com.github.vitalibo.alarm.processor.core.model.Rule;
import com.github.vitalibo.alarm.processor.core.stream.AlarmStream;
import com.github.vitalibo.alarm.processor.core.util.ScalaTypes;
import com.github.vitalibo.alarm.processor.core.util.function.Supplier;
import com.github.vitalibo.alarm.processor.infrastructure.aws.cloudwatch.CloudWatchMetricsListener;
import com.github.vitalibo.alarm.processor.infrastructure.aws.dms.DMSEvent;
import com.github.vitalibo.alarm.processor.infrastructure.aws.dms.EventLogSource;
import com.github.vitalibo.alarm.processor.infrastructure.aws.dms.EventLogTranslator;
import com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.KinesisRecordsPublisher;
import com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.KinesisStreamSink;
import com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.KinesisStreamSource;
import com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.transform.DMSEventTranslator;
import com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.transform.MetricTranslator;
import com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.transform.PutRecordsRequestEntryTranslator;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Arrays;
import java.util.Objects;

public final class Factory {

    private static final String AWS_REGION = "aws.region";

    private static final String SPARK_MASTER = "spark.master";
    private static final String SPARK_APPLICATION_NAME = "spark.app.name";
    private static final String SPARK_CHECKPOINT_DIRECTORY = "spark.checkpoint.dir";
    private static final String SPARK_STREAMING_BATCH_DURATION_IN_MILLIS = "spark.streaming.batch.duration";

    private static final String METRIC_STREAM_NAME = "aws.kinesis.metric.stream.name";
    private static final String METRIC_STREAM_INITIAL_POSITION = "aws.kinesis.metric.initial.position";
    private static final String METRIC_STREAM_CHECKPOINT_INTERVAL_IN_MILLIS = "aws.kinesis.metric.checkpoint.interval";
    private static final String METRIC_STREAM_CHECKPOINT_APPLICATION_NAME = "aws.kinesis.metric.checkpoint.app.name";

    private static final String RULE_EVENT_LOG_STREAM_NAME = "aws.kinesis.rule.stream.name";
    private static final String RULE_EVENT_LOG_STREAM_INITIAL_POSITION = "aws.kinesis.rule.initial.position";
    private static final String RULE_EVENT_LOG_STREAM_CHECKPOINT_INTERVAL_IN_MILLIS = "aws.kinesis.rule.checkpoint.interval";
    private static final String RULE_EVENT_LOG_STREAM_CHECKPOINT_APPLICATION_NAME = "aws.kinesis.rule.checkpoint.app.name";

    private static final String ALARM_STREAM_NAME = "aws.kinesis.alarm.stream.name";
    private static final String ALARM_STREAM_PUBLISHER_BUFFER_SIZE = "aws.kinesis.alarm.publisher.buffer.size";

    @Getter(lazy = true)
    private static final Factory instance = new Factory(ConfigFactory.load(), ConfigFactory.parseResources("default-application.conf"));

    private final Config config;
    private final AmazonKinesis kinesis;
    private final AmazonCloudWatchAsync cloudWatchAsync;

    Factory(Config... configs) {
        this.config = Arrays.stream(configs)
            .reduce(Config::withFallback)
            .orElseThrow(IllegalStateException::new)
            .resolve();
        this.kinesis = AmazonKinesisClient.builder()
            .withRegion(config.getString(AWS_REGION))
            .build();
        this.cloudWatchAsync = AmazonCloudWatchAsyncClient.asyncBuilder()
            .withRegion(config.getString(AWS_REGION))
            .build();
    }

    public Spark createSpark() {
        SparkConf conf = new SparkConf();
        conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        conf.set("spark.kryo.classesToRegister", ScalaTypes.classes(Alarm.class, EventLog.class, Metric.class, Rule.class));
        conf.set("spark.streaming.stopGracefullyOnShutdown", "true");

        final String name = config.getString(SPARK_APPLICATION_NAME);
        if (Objects.nonNull(name)) {
            conf.setAppName(name);
        }

        final String master = config.getString(SPARK_MASTER);
        if (Objects.nonNull(master) && master.startsWith("local")) {
            conf.setMaster(master);
        }

        final JavaStreamingContext context = new JavaStreamingContext(
            new JavaSparkContext(conf),
            new Duration(config.getInt(SPARK_STREAMING_BATCH_DURATION_IN_MILLIS)));

        context.addStreamingListener(
            new CloudWatchMetricsListener(
                context.sparkContext().appName(),
                cloudWatchAsync));

        return new Spark(context);
    }

    public Stream createAlarmStream() {
        return new AlarmStream(
            new KinesisStreamSource<>(
                kinesis,
                new <Metric>KinesisStreamSource.StreamBuilder()
                    .withRegion(config.getString(AWS_REGION))
                    .withStreamName(config.getString(METRIC_STREAM_NAME))
                    .withApplicationName(config.getString(METRIC_STREAM_CHECKPOINT_APPLICATION_NAME))
                    .withCheckpointInterval(new Duration(config.getLong(METRIC_STREAM_CHECKPOINT_INTERVAL_IN_MILLIS)))
                    .withInitialPosition(InitialPositionInStream.valueOf(config.getString(METRIC_STREAM_INITIAL_POSITION))),
                MetricTranslator::fromKinesisRecord),
            new EventLogSource<>(
                new KinesisStreamSource<>(
                    kinesis,
                    new <DMSEvent>KinesisStreamSource.StreamBuilder()
                        .withRegion(config.getString(AWS_REGION))
                        .withStreamName(config.getString(RULE_EVENT_LOG_STREAM_NAME))
                        .withApplicationName(config.getString(RULE_EVENT_LOG_STREAM_CHECKPOINT_APPLICATION_NAME))
                        .withCheckpointInterval(new Duration(config.getLong(RULE_EVENT_LOG_STREAM_CHECKPOINT_INTERVAL_IN_MILLIS)))
                        .withInitialPosition(InitialPositionInStream.valueOf(config.getString(RULE_EVENT_LOG_STREAM_INITIAL_POSITION))),
                    DMSEventTranslator::fromKinesisRecord),
                EventLogTranslator::fromDMSEvent),
            new KinesisStreamSink<>(
                new KinesisRecordsPublisher.Builder<Alarm>()
                    .withRegion(config.getString(AWS_REGION))
                    .withStreamName(config.getString(ALARM_STREAM_NAME))
                    .withRecordTranslator(PutRecordsRequestEntryTranslator::fromAlarm)
                    .withMaxBufferSize(config.getInt(ALARM_STREAM_PUBLISHER_BUFFER_SIZE))
                    .withKinesisAsyncSupplier(createAmazonKinesisAsyncSupplier(config.getString(AWS_REGION)))),
            config.getString(SPARK_CHECKPOINT_DIRECTORY));
    }

    private static Supplier<AmazonKinesisAsync> createAmazonKinesisAsyncSupplier(String region) {
        return () -> AmazonKinesisAsyncClient.asyncBuilder()
            .withRegion(region)
            .build();
    }

}