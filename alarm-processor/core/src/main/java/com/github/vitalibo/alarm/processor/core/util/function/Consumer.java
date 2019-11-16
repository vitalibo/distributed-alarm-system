package com.github.vitalibo.alarm.processor.core.util.function;

import org.apache.spark.api.java.function.VoidFunction;

import java.io.Serializable;

@FunctionalInterface
public interface Consumer<T> extends java.util.function.Consumer<T>, VoidFunction<T>, Serializable {

    @Override
    default void call(T t) {
        accept(t);
    }
}