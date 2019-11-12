package com.github.vitalibo.alarm.processor.core;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.Serializable;

@FunctionalInterface
public interface Source<T> extends Serializable {

    JavaDStream<T> read(JavaStreamingContext context);

}