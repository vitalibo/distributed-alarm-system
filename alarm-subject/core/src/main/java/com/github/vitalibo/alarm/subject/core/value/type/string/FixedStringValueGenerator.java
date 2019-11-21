package com.github.vitalibo.alarm.subject.core.value.type.string;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.util.Objects;

public class FixedStringValueGenerator implements ValueGenerator<String> {

    private final String value;

    public FixedStringValueGenerator(String value) {
        Objects.requireNonNull(value, "`value` must be defined.");
        this.value = value;
    }

    @Override
    public String generate() {
        return value;
    }

}