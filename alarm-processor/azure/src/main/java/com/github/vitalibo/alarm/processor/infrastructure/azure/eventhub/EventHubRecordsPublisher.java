package com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub;

import com.github.vitalibo.alarm.processor.core.util.function.Function;
import com.github.vitalibo.alarm.processor.core.util.function.Singleton;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Wither;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EventHubRecordsPublisher<T> implements AutoCloseable {

    private final Integer maxBufferSize;
    private final EventHubClient eventhub;
    private final Function<T, EventData> translator;

    private final List<T> buffer;

    public synchronized void publish(T item) {
        buffer.add(item);
        if (buffer.size() < maxBufferSize) {
            return;
        }

        flush();
    }

    @SneakyThrows
    public synchronized void flush() {
        if (buffer.isEmpty()) {
            return;
        }

        eventhub.send(
            buffer.stream()
                .map(translator::apply)
                .collect(Collectors.toList()));

        buffer.clear();
    }

    @Override
    @SneakyThrows
    public void close() {
        flush();
    }

    @Data
    @Wither
    @RequiredArgsConstructor
    @EqualsAndHashCode(of = {"namespaceName", "eventHubName"})
    public static class Builder<T> implements Singleton<EventHubRecordsPublisher<T>> {

        private final String namespaceName;
        private final String eventHubName;
        private final String sasKeyName;
        private final String sasKey;
        private final Integer maxBufferSize;
        private final Integer scheduledThreadPoolSize;
        private final Function<T, EventData> translator;

        public Builder() {
            this(null, null, "RootManageSharedAccessKey", null, 100, 10, null);
        }

        public EventHubRecordsPublisher<T> build() {
            Objects.requireNonNull(namespaceName, "namespaceName");
            Objects.requireNonNull(eventHubName, "eventHubName");
            Objects.requireNonNull(sasKeyName, "sasKeyName");
            Objects.requireNonNull(sasKey, "sasKey");
            Objects.requireNonNull(maxBufferSize, "maxBufferSize");
            Objects.requireNonNull(scheduledThreadPoolSize, "scheduledThreadPoolSize");
            Objects.requireNonNull(translator, "translator");

            return build(
                maxBufferSize,
                String.valueOf(new ConnectionStringBuilder()
                    .setNamespaceName(namespaceName)
                    .setEventHubName(eventHubName)
                    .setSasKeyName(sasKeyName)
                    .setSasKey(sasKey)),
                translator);
        }

        @SneakyThrows
        EventHubRecordsPublisher<T> build(int maxBufferSize, String connectionString, Function<T, EventData> translator) {
            return new EventHubRecordsPublisher<>(
                maxBufferSize,
                EventHubClient.createFromConnectionStringSync(
                    connectionString,
                    Executors.newScheduledThreadPool(scheduledThreadPoolSize)),
                translator,
                new ArrayList<>());
        }

        @Override
        public EventHubRecordsPublisher<T> get() {
            return build();
        }
    }

}