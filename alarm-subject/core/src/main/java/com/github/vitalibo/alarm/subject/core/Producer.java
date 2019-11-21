package com.github.vitalibo.alarm.subject.core;

import java.util.List;
import java.util.function.Consumer;

@FunctionalInterface
public interface Producer extends Consumer<String> {

    void send(String value);

    @Override
    default void accept(String value) {
        this.send(value);
    }

    default void sendAll(List<String> values) {
        values.forEach(this::send);
    }

}