package com.github.vitalibo.alarm.processor.core;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.apache.spark.HashPartitioner;
import org.apache.spark.Partitioner;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public class Spark implements Serializable, AutoCloseable {

    @Delegate
    private final JavaStreamingContext context;

    public void submit(Stream stream) {
        stream.process(this);
    }

    public <T> JavaDStream<T> createStream(Source<T> source) {
        return source.read(context);
    }

    public <T> void writeStream(Sink<T> sink, JavaDStream<T> stream) {
        sink.write(context, stream);
    }

    public Partitioner defaultPartitioner() {
        return new HashPartitioner(defaultParallelism());
    }

    public Integer defaultParallelism() {
        return sparkContext().defaultParallelism();
    }

    public <T> JavaRDD<T> parallelize(List<T> list) {
        return sparkContext().parallelize(list);
    }

    public <K, V> JavaPairRDD<K, V> parallelizePairs(List<Tuple2<K, V>> list) {
        return sparkContext().parallelizePairs(list);
    }
}