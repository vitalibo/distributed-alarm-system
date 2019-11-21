package com.github.vitalibo.alarm.subject.core.value.type.timestamp;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Random;

public class RandomTimestampValueGenerator implements ValueGenerator<OffsetDateTime> {

    private final Random random = new Random();

    private final OffsetDateTime minValue;
    private final int range;

    public RandomTimestampValueGenerator(OffsetDateTime minValue, OffsetDateTime maxValue) {
        Objects.requireNonNull(minValue, "`minValue` must be defined.");
        Objects.requireNonNull(maxValue, "`maxValue` must be defined.");
        if (minValue.isAfter(maxValue)) {
            throw new IllegalArgumentException("`minValue` must be less or equal to `maxValue`.");
        }

        this.minValue = minValue;
        this.range = (int) ChronoUnit.SECONDS.between(minValue, maxValue);
    }

    @Override
    public OffsetDateTime generate() {
        return minValue.plus(random.nextInt(range), ChronoUnit.SECONDS);
    }

}