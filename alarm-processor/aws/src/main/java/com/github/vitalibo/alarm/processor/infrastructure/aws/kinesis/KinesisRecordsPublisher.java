package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.amazonaws.services.kinesis.model.PutRecordsResultEntry;
import com.github.vitalibo.alarm.processor.core.util.function.Function;
import com.github.vitalibo.alarm.processor.core.util.function.Singleton;
import com.github.vitalibo.alarm.processor.core.util.function.Supplier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Wither;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class KinesisRecordsPublisher<T> implements AutoCloseable {

    private final String streamName;
    private final Integer maxBufferSize;
    private final AmazonKinesisAsync kinesisAsync;
    private final Function<T, PutRecordsRequestEntry> recordTranslator;

    private final PutRecordsAsyncHandler asyncRequestHandler;
    private final List<T> buffer;

    KinesisRecordsPublisher(String streamName, Integer maxBufferSize, AmazonKinesisAsync kinesisAsync,
                            Function<T, PutRecordsRequestEntry> recordTranslator) {
        this.streamName = streamName;
        this.maxBufferSize = maxBufferSize;
        this.kinesisAsync = kinesisAsync;
        this.recordTranslator = recordTranslator;
        this.asyncRequestHandler = new PutRecordsAsyncHandler(3);
        this.buffer = new ArrayList<>(maxBufferSize);
    }

    public synchronized void publish(T item) {
        buffer.add(item);
        if (buffer.size() < maxBufferSize) {
            return;
        }

        flush();
    }

    public synchronized void flush() {
        if (buffer.isEmpty()) {
            return;
        }

        kinesisAsync.putRecordsAsync(
            new PutRecordsRequest()
                .withStreamName(streamName)
                .withRecords(buffer.stream()
                    .map(recordTranslator)
                    .collect(Collectors.toList())),
            asyncRequestHandler);

        buffer.clear();
    }

    @Override
    public void close() {
        flush();
    }

    @RequiredArgsConstructor
    public class PutRecordsAsyncHandler implements AsyncHandler<PutRecordsRequest, PutRecordsResult> {

        private final int retryAttempts;

        @Override
        @SneakyThrows
        public void onError(Exception exception) {
            throw exception;
        }

        @Override
        public void onSuccess(PutRecordsRequest request, PutRecordsResult response) {
            if (response.getFailedRecordCount() == 0) {
                return;
            }

            if (retryAttempts == 0) {
                throw new RuntimeException("Failed retry put records");
            }

            List<PutRecordsRequestEntry> records = new ArrayList<>(request.getRecords());
            List<PutRecordsResultEntry> unprocessed = response.getRecords();
            for (int i = unprocessed.size() - 1; i >= 0; i--) {
                if (Objects.nonNull(unprocessed.get(i).getShardId())) {
                    records.remove(i);
                }
            }

            kinesisAsync.putRecordsAsync(
                request.withRecords(records), new PutRecordsAsyncHandler(retryAttempts - 1));
        }
    }

    @Data
    @Wither
    @RequiredArgsConstructor
    @EqualsAndHashCode(of = {"region", "streamName"})
    public static class Builder<T> implements Singleton<KinesisRecordsPublisher<T>> {

        private final String region;
        private final String streamName;
        private final Integer maxBufferSize;
        private final Supplier<AmazonKinesisAsync> kinesisAsyncSupplier;
        private final Function<T, PutRecordsRequestEntry> recordTranslator;

        public Builder() {
            this(null, null, null, null, null);
        }

        public KinesisRecordsPublisher<T> build() {
            Objects.requireNonNull(region, "region");
            Objects.requireNonNull(streamName, "streamName");
            Objects.requireNonNull(maxBufferSize, "maxBufferSize");
            Objects.requireNonNull(kinesisAsyncSupplier, "kinesisAsyncSupplier");
            Objects.requireNonNull(recordTranslator, "recordTranslator");

            return build(streamName, maxBufferSize, kinesisAsyncSupplier.get(), recordTranslator);
        }

        KinesisRecordsPublisher<T> build(String streamName, Integer maxBufferSize, AmazonKinesisAsync kinesisAsync,
                                         Function<T, PutRecordsRequestEntry> recordTranslator) {
            return new KinesisRecordsPublisher<>(streamName, maxBufferSize, kinesisAsync, recordTranslator);
        }

        @Override
        public KinesisRecordsPublisher<T> get() {
            return build();
        }
    }
}