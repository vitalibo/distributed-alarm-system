package com.github.vitalibo.alarm.subject.core.value.type.timestamp;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Supplier;

public class NaturalGrowthTimestampValueGenerator implements ValueGenerator<OffsetDateTime> {

    private final OffsetDateTime startAt;
    private final Supplier<Long> diffInMillis;

    public NaturalGrowthTimestampValueGenerator(OffsetDateTime startAt) {
        this(startAt, System.currentTimeMillis());
    }

    NaturalGrowthTimestampValueGenerator(OffsetDateTime startAt, long initialTimeMillis) {
        this(startAt, () -> System.currentTimeMillis() - initialTimeMillis);
    }

    NaturalGrowthTimestampValueGenerator(OffsetDateTime startAt, Supplier<Long> diffInMillis) {
        Objects.requireNonNull(startAt, "`startAt` must be defined.");
        this.startAt = startAt;
        this.diffInMillis = diffInMillis;
    }

    @Override
    public OffsetDateTime generate() {
        return startAt.plus(diffInMillis.get(), ChronoUnit.MILLIS);
    }

}