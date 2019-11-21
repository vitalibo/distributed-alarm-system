package com.github.vitalibo.alarm.subject.core.value.type.number;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.util.Objects;
import java.util.Random;

public class RandomDoubleValueGenerator implements ValueGenerator<Double> {

    private final Random random = new Random();

    private final double minValue;
    private final double range;

    public RandomDoubleValueGenerator(Double minValue, Double maxValue) {
        Objects.requireNonNull(minValue, "`minValue` must be defined.");
        Objects.requireNonNull(maxValue, "`maxValue` must be defined.");
        if (maxValue < minValue) {
            throw new IllegalArgumentException("`minValue` must be less or equal to `maxValue`.");
        }

        this.minValue = minValue;
        this.range = maxValue - minValue;
    }

    @Override
    public Double generate() {
        return minValue + random.nextDouble() * range;
    }

}