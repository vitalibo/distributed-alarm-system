package com.github.vitalibo.alarm.subject.core.value.format.timestamp;

import com.github.vitalibo.alarm.subject.core.value.ValueFormatter;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Function;

public class EpochTimestampValueFormatter implements ValueFormatter<OffsetDateTime, Long> {

    private final Function<OffsetDateTime, Long> function;

    public EpochTimestampValueFormatter(ChronoUnit unit) {
        Objects.requireNonNull(unit, "`unit` must be defined.");
        switch (unit) {
            case SECONDS:
                this.function = OffsetDateTime::toEpochSecond;
                break;
            case MILLIS:
                this.function = o -> o.toInstant().toEpochMilli();
                break;
            default:
                throw new IllegalArgumentException("Supported value: [SECONDS, MILLIS].");
        }
    }

    @Override
    public Long format(OffsetDateTime timestamp) {
        return function.apply(timestamp);
    }

}