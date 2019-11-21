package com.github.vitalibo.alarm.subject.core.value.type.number;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.util.Objects;
import java.util.function.Function;

public class FunctionDoubleValueGenerator implements ValueGenerator<Double> {

    private final Function<Double, Double> function;
    private final double step;

    private double x;

    public FunctionDoubleValueGenerator(String function, Double startValue, Double step) {
        this(fn(function), startValue, step);
    }

    FunctionDoubleValueGenerator(Function<Double, Double> function, Double startValue, Double step) {
        Objects.requireNonNull(startValue, "`startValue` must be defined.");
        Objects.requireNonNull(step, "`step` must be defined.");
        this.function = function;
        this.step = step;
        this.x = startValue;
    }

    @Override
    public Double generate() {
        double y = function.apply(x);
        x += step;
        return y;
    }

    private static Function<Double, Double> fn(String function) {
        Objects.requireNonNull(function, "`function` must be defined.");
        switch (function) {
            case "sin(X)":
                return x -> Math.sin(Math.toRadians(x));
            case "cos(X)":
                return x -> Math.cos(Math.toRadians(x));
            case "sin(2X)-2sin(X)":
                return x -> Math.sin(Math.toRadians(2 * x)) - 2 * Math.sin(Math.toRadians(x));
            default:
                throw new IllegalArgumentException("Unknown formula.");
        }
    }

}