package com.github.vitalibo.alarm.subject.core.value.type.number;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.util.Objects;

public class FixedLongValueGenerator implements ValueGenerator<Long> {

    private final Long value;

    public FixedLongValueGenerator(Long value) {
        Objects.requireNonNull(value, "`value` must be defined.");
        this.value = value;
    }

    @Override
    public Long generate() {
        return value;
    }

}