package com.github.vitalibo.alarm.subject.core.value;

import java.util.function.Supplier;

@FunctionalInterface
public interface ValueGenerator<T> extends Supplier<T> {

    T generate();

    @Override
    default T get() {
        return generate();
    }

}