package com.github.vitalibo.alarm.subject.core.value.type.number;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.util.Objects;

public class IncrementalValueGenerator implements ValueGenerator<Long> {

    private final int step;

    private long value;

    public IncrementalValueGenerator(Long startValue, Integer step) {
        Objects.requireNonNull(startValue, "`startValue` must be defined.");
        Objects.requireNonNull(step, "`step` must be defined.");
        this.value = startValue;
        this.step = step;
    }

    @Override
    public Long generate() {
        long v = value;
        value += step;
        return v;
    }

}