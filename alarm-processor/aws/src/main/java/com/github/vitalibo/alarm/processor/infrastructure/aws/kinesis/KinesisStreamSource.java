package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.Record;
import com.github.vitalibo.alarm.processor.core.Source;
import com.github.vitalibo.alarm.processor.core.util.ScalaTypes;
import com.github.vitalibo.alarm.processor.core.util.function.Function;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kinesis.KinesisInitialPositions;
import org.apache.spark.streaming.kinesis.KinesisInputDStream;
import scala.reflect.ClassTag;

import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class KinesisStreamSource<T> implements Source<T> {

    private final AmazonKinesis kinesis;
    private final StreamBuilder builder;
    private final Function<Record, T> translator;

    @Override
    public JavaDStream<T> read(JavaStreamingContext context) {
        int numShards = describeNumShards(kinesis, builder.getStreamName());

        return Stream.generate(() -> builder.build(context))
            .limit(numShards)
            .reduce(JavaDStream::union)
            .orElseThrow(() -> new IllegalStateException("Kinesis stream don't have any shards"))
            .filter(Objects::nonNull)
            .map(translator);
    }

    private static int describeNumShards(AmazonKinesis kinesis, String streamName) {
        return kinesis
            .describeStream(
                new DescribeStreamRequest()
                    .withStreamName(streamName))
            .getStreamDescription()
            .getShards()
            .size();
    }

    @Data
    @Wither
    @RequiredArgsConstructor
    public static class StreamBuilder {

        private final String region;
        private final String streamName;
        private final String applicationName;
        private final InitialPositionInStream initialPosition;
        private final Duration checkpointInterval;

        public StreamBuilder() {
            this(null, null, null, InitialPositionInStream.TRIM_HORIZON, new Duration(1000));
        }

        JavaDStream<Record> build(JavaStreamingContext context) {
            Objects.requireNonNull(region, "region");
            Objects.requireNonNull(streamName, "streamName");
            Objects.requireNonNull(applicationName, "applicationName");

            return build(
                KinesisInputDStream.builder()
                    .streamingContext(context)
                    .checkpointAppName(applicationName)
                    .streamName(streamName)
                    .endpointUrl(String.format("https://kinesis.%s.amazonaws.com", region))
                    .regionName(region)
                    .initialPosition(KinesisInitialPositions.fromKinesisInitialPosition(initialPosition))
                    .checkpointInterval(checkpointInterval)
                    .storageLevel(StorageLevel.MEMORY_AND_DISK_2()),
                ScalaTypes.classTag(Record.class));
        }

        JavaDStream<Record> build(KinesisInputDStream.Builder builder, ClassTag<Record> classTag) {
            return JavaDStream.fromDStream(
                builder.buildWithMessageHandler(
                    ScalaTypes.function(o -> o), classTag),
                classTag);
        }
    }
}