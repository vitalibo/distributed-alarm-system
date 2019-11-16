package com.github.vitalibo.alarm.processor.core.util.function;

import org.apache.spark.api.java.function.FilterFunction;

import java.io.Serializable;

@FunctionalInterface
public interface Predicate<T> extends java.util.function.Predicate<T>, FilterFunction<T>, Serializable {

    @Override
    default boolean call(T value) {
        return test(value);
    }
}