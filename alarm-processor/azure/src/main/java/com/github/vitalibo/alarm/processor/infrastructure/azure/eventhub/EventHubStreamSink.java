package com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub;

import com.github.vitalibo.alarm.processor.core.Sink;
import com.github.vitalibo.alarm.processor.core.util.function.Singleton;
import lombok.RequiredArgsConstructor;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Iterator;

@RequiredArgsConstructor
public class EventHubStreamSink<T> implements Sink<T> {

    private final Singleton<EventHubRecordsPublisher<T>> singletonPublisher;

    @Override
    public void write(JavaStreamingContext context, JavaDStream<T> stream) {
        stream.foreachRDD(rdd -> rdd.foreachPartition(this::write));
    }

    void write(Iterator<T> iterator) {
        if (!iterator.hasNext()) {
            return;
        }

        try (EventHubRecordsPublisher<T> publisher = singletonPublisher.createOrGet()) {
            iterator.forEachRemaining(publisher::publish);
        }
    }
}