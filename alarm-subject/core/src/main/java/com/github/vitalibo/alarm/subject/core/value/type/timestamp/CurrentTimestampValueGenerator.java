package com.github.vitalibo.alarm.subject.core.value.type.timestamp;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class CurrentTimestampValueGenerator implements ValueGenerator<OffsetDateTime> {

    private static final ZoneId UTC = ZoneId.of("UTC");

    @Override
    public OffsetDateTime generate() {
        return OffsetDateTime.now(UTC);
    }

}