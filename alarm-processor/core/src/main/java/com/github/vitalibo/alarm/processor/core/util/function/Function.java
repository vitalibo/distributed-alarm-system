package com.github.vitalibo.alarm.processor.core.util.function;

import java.io.Serializable;

@FunctionalInterface
public interface Function<T, R> extends java.util.function.Function<T, R>, org.apache.spark.api.java.function.Function<T, R>, Serializable {

    @Override
    default R call(T t) {
        return apply(t);
    }
}