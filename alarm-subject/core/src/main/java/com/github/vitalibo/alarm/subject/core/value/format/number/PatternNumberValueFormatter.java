package com.github.vitalibo.alarm.subject.core.value.format.number;

import com.github.vitalibo.alarm.subject.core.value.ValueFormatter;

import java.util.Objects;

public class PatternNumberValueFormatter implements ValueFormatter<Number, String> {

    private final String pattern;

    public PatternNumberValueFormatter(String pattern) {
        Objects.requireNonNull(pattern, "`pattern` must be defined.");
        this.pattern = pattern;
    }

    @Override
    public String format(Number number) {
        return String.format(pattern, number);
    }

}