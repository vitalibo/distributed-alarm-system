package com.github.vitalibo.alarm.subject.core.value.format.timestamp;

import com.github.vitalibo.alarm.subject.core.value.ValueFormatter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class PatternTimestampValueFormatter implements ValueFormatter<OffsetDateTime, String> {

    private final DateTimeFormatter pattern;

    public PatternTimestampValueFormatter(String pattern) {
        Objects.requireNonNull(pattern, "`pattern` must be defined.");

        try {
            Object o = DateTimeFormatter.class.getField(pattern).get(null);
            this.pattern = (DateTimeFormatter) o;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(
                String.format("Unknown pattern `%s`. %s", pattern, e.getMessage()));
        }
    }

    @Override
    public String format(OffsetDateTime timestamp) {
        return pattern.format(timestamp);
    }

}