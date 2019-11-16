package com.github.vitalibo.alarm.processor.core.util.function;

import org.apache.spark.api.java.function.Function0;

import java.io.Serializable;

@FunctionalInterface
public interface Supplier<T> extends java.util.function.Supplier<T>, Function0<T>, Serializable {

    @Override
    default T call() {
        return get();
    }
}