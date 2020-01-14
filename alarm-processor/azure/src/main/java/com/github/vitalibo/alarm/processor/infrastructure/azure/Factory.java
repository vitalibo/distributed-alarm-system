package com.github.vitalibo.alarm.processor.infrastructure.azure;

import com.github.vitalibo.alarm.processor.core.Spark;
import com.github.vitalibo.alarm.processor.core.Stream;
import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.model.Metric;
import com.github.vitalibo.alarm.processor.core.model.Rule;
import com.github.vitalibo.alarm.processor.core.stream.AlarmStream;
import com.github.vitalibo.alarm.processor.core.util.ScalaTypes;
import com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb.ChangeFeed;
import com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb.EventLogSource;
import com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb.transform.EventLogTranslator;
import com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.EventHubRecordsPublisher;
import com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.EventHubStreamSink;
import com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.EventHubStreamSource;
import com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.transform.ChangeFeedTranslator;
import com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.transform.EventDataTranslator;
import com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.transform.MetricTranslator;
import com.github.vitalibo.alarm.processor.infrastructure.azure.mock.AlarmStoreMock;
import com.github.vitalibo.alarm.processor.infrastructure.azure.mock.RuleStoreMock;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Arrays;
import java.util.Objects;

public class Factory {

    private static final String SPARK_MASTER = "spark.master";
    private static final String SPARK_APPLICATION_NAME = "spark.app.name";
    private static final String SPARK_CHECKPOINT_DIRECTORY = "spark.checkpoint.dir";
    private static final String SPARK_STREAMING_BATCH_DURATION_IN_MILLIS = "spark.streaming.batch.duration";

    private static final String METRIC_EVENT_HUB_NAMESPACE = "azure.eventhub.metric.namespace";
    private static final String METRIC_EVENT_HUB_NAME = "azure.eventhub.metric.name";
    private static final String METRIC_EVENT_HUB_SAS_NAME = "azure.eventhub.metric.sas.name";
    private static final String METRIC_EVENT_HUB_SAS_KEY = "azure.eventhub.metric.sas.key";

    private static final String RULE_EVENT_LOG_EVENT_HUB_NAMESPACE = "azure.eventhub.rule.namespace";
    private static final String RULE_EVENT_LOG_EVENT_HUB_NAME = "azure.eventhub.rule.name";
    private static final String RULE_EVENT_LOG_EVENT_HUB_SAS_NAME = "azure.eventhub.rule.sas.name";
    private static final String RULE_EVENT_LOG_EVENT_HUB_SAS_KEY = "azure.eventhub.rule.sas.key";

    private static final String ALARM_EVENT_HUB_NAMESPACE = "azure.eventhub.alarm.namespace";
    private static final String ALARM_EVENT_HUB_NAME = "azure.eventhub.alarm.name";
    private static final String ALARM_EVENT_HUB_SAS_NAME = "azure.eventhub.alarm.sas.name";
    private static final String ALARM_EVENT_HUB_SAS_KEY = "azure.eventhub.alarm.sas.key";
    private static final String ALARM_EVENT_HUB_PUBLISHER_BUFFER_SIZE = "azure.eventhub.alarm.publisher.bufferSize";
    private static final String ALARM_EVENT_HUB_PUBLISHER_POOL_SIZE = "azure.eventhub.alarm.publisher.poolSize";

    @Getter(lazy = true)
    private static final Factory instance = new Factory(ConfigFactory.load(), ConfigFactory.parseResources("default-application.conf"));

    private final Config config;

    Factory(Config... configs) {
        this.config = Arrays.stream(configs)
            .reduce(Config::withFallback)
            .orElseThrow(IllegalStateException::new)
            .resolve();
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

        return new Spark(
            new JavaStreamingContext(
                new JavaSparkContext(conf),
                new Duration(config.getInt(SPARK_STREAMING_BATCH_DURATION_IN_MILLIS))));
    }

    public Stream createAlarmStream() {
        return new AlarmStream(
            new EventHubStreamSource<Metric>()
                .withNamespaceName(config.getString(METRIC_EVENT_HUB_NAMESPACE))
                .withEventHubName(config.getString(METRIC_EVENT_HUB_NAME))
                .withSasKeyName(config.getString(METRIC_EVENT_HUB_SAS_NAME))
                .withSasKey(config.getString(METRIC_EVENT_HUB_SAS_KEY))
                .withTranslator(MetricTranslator::fromEventData),
            new EventLogSource<>(
                new EventHubStreamSource<ChangeFeed>()
                    .withNamespaceName(config.getString(RULE_EVENT_LOG_EVENT_HUB_NAMESPACE))
                    .withEventHubName(config.getString(RULE_EVENT_LOG_EVENT_HUB_NAME))
                    .withSasKeyName(config.getString(RULE_EVENT_LOG_EVENT_HUB_SAS_NAME))
                    .withSasKey(config.getString(RULE_EVENT_LOG_EVENT_HUB_SAS_KEY))
                    .withTranslator(ChangeFeedTranslator::fromEventData),
                EventLogTranslator::fromChangeFeed),
            new EventHubStreamSink<>(
                new EventHubRecordsPublisher.Builder<Alarm>()
                    .withNamespaceName(config.getString(ALARM_EVENT_HUB_NAMESPACE))
                    .withEventHubName(config.getString(ALARM_EVENT_HUB_NAME))
                    .withSasKeyName(config.getString(ALARM_EVENT_HUB_SAS_NAME))
                    .withSasKey(config.getString(ALARM_EVENT_HUB_SAS_KEY))
                    .withMaxBufferSize(config.getInt(ALARM_EVENT_HUB_PUBLISHER_BUFFER_SIZE))
                    .withScheduledThreadPoolSize(config.getInt(ALARM_EVENT_HUB_PUBLISHER_POOL_SIZE))
                    .withTranslator(EventDataTranslator::fromAlarm)),
            new AlarmStoreMock(),
            new RuleStoreMock(),
            config.getString(SPARK_CHECKPOINT_DIRECTORY));
    }

}