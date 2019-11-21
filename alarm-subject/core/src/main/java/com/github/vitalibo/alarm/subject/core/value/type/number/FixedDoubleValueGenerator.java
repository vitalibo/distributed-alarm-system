package com.github.vitalibo.alarm.subject.core.value.type.number;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.util.Objects;

public class FixedDoubleValueGenerator implements ValueGenerator<Double> {

    private final Double value;

    public FixedDoubleValueGenerator(Double value) {
        Objects.requireNonNull(value, "`value` must be defined.");
        this.value = value;
    }

    @Override
    public Double generate() {
        return value;
    }

}