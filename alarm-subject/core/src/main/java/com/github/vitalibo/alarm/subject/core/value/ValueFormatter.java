package com.github.vitalibo.alarm.subject.core.value;

import java.util.function.Function;

@FunctionalInterface
public interface ValueFormatter<T, R> extends Function<T, R> {

    R format(T t);

    @Override
    default R apply(T t) {
        return format(t);
    }

}