package com.github.vitalibo.alarm.subject.core.value.type.number;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.util.Objects;
import java.util.Random;

public class RandomLongValueGenerator implements ValueGenerator<Long> {

    private final Random random = new Random();

    private final long minValue;
    private final long range;

    public RandomLongValueGenerator(Long minValue, Long maxValue) {
        Objects.requireNonNull(minValue, "`minValue` must be defined.");
        Objects.requireNonNull(maxValue, "`maxValue` must be defined.");
        if (maxValue < minValue) {
            throw new IllegalArgumentException("`minValue` must be less or equal to `maxValue`.");
        }

        this.minValue = minValue;
        this.range = maxValue - minValue;
    }

    @Override
    public Long generate() {
        return minValue + (long) (random.nextDouble() * range);
    }

}