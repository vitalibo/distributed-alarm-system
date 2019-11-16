package com.github.vitalibo.alarm.processor.core.util.function;

import org.apache.spark.api.java.function.VoidFunction2;

import java.io.Serializable;

@FunctionalInterface
public interface BiConsumer<T, U> extends java.util.function.BiConsumer<T, U>, VoidFunction2<T, U>, Serializable {

    @Override
    default void call(T t, U u) {
        accept(t, u);
    }
}