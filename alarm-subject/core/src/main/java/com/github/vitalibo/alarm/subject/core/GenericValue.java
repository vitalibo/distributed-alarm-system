package com.github.vitalibo.alarm.subject.core;

import com.github.vitalibo.alarm.subject.core.value.ValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;
import lombok.Data;

import java.util.function.Supplier;

@Data
public class GenericValue<T, R> implements Supplier<R> {

    private final ValueGenerator<T> generator;
    private final ValueFormatter<T, R> formatter;

    @Override
    public R get() {
        T value = generator.generate();
        return formatter.format(value);
    }

}