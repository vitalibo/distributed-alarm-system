package com.github.vitalibo.alarm.subject.core.value.format.number;

import com.github.vitalibo.alarm.subject.core.value.ValueFormatter;

import java.util.Objects;

public class PrecisionDoubleValueFormatter implements ValueFormatter<Number, Double> {

    private final double precision;

    public PrecisionDoubleValueFormatter(String precision) {
        Objects.requireNonNull(precision, "`precision` must be defined.");
        if (!precision.matches("0\\.0*1")) {
            throw new IllegalArgumentException("`precision` not matches pattern.");
        }

        this.precision = 1 / Double.parseDouble(precision);
    }

    @Override
    public Double format(Number number) {
        return (int) (number.doubleValue() * precision) / precision;
    }

}