package com.github.vitalibo.alarm.subject.core.value.type.timestamp;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.time.OffsetDateTime;
import java.util.Objects;

public class FixedTimestampValueGenerator implements ValueGenerator<OffsetDateTime> {

    private final OffsetDateTime value;

    public FixedTimestampValueGenerator(OffsetDateTime value) {
        Objects.requireNonNull(value, "`value` must be defined.");
        this.value = value;
    }

    @Override
    public OffsetDateTime generate() {
        return value;
    }

}