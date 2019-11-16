package com.github.vitalibo.alarm.processor.core.util.function;

import org.apache.spark.api.java.function.Function2;

import java.io.Serializable;

@FunctionalInterface
public interface BiFunction<T, U, R> extends java.util.function.BiFunction<T, U, R>, Function2<T, U, R>, Serializable {

    @Override
    default R call(T t, U u) {
        return apply(t, u);
    }
}