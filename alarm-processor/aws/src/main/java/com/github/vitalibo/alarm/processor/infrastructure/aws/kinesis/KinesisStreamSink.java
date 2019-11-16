package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis;

import com.github.vitalibo.alarm.processor.core.Sink;
import com.github.vitalibo.alarm.processor.core.util.function.Singleton;
import lombok.RequiredArgsConstructor;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.Serializable;
import java.util.Iterator;

@RequiredArgsConstructor
public class KinesisStreamSink<T> implements Sink<T>, Serializable {

    private final Singleton<KinesisRecordsPublisher<T>> singletonPublisher;

    @Override
    public void write(JavaStreamingContext context, JavaDStream<T> stream) {
        stream.foreachRDD(rdd -> rdd.foreachPartition(this::write));
    }

    void write(Iterator<T> iterator) {
        if (!iterator.hasNext()) {
            return;
        }

        try (KinesisRecordsPublisher<T> publisher = singletonPublisher.createOrGet()) {
            iterator.forEachRemaining(publisher::publish);
        }
    }

}