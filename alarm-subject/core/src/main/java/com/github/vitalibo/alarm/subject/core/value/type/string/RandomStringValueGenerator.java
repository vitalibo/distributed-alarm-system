package com.github.vitalibo.alarm.subject.core.value.type.string;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.util.Objects;
import java.util.Random;

public class RandomStringValueGenerator implements ValueGenerator<String> {

    private final Random random = new Random();

    private final int minLength;
    private final int range;
    private final char[] source;

    public RandomStringValueGenerator(Integer minLength, Integer maxLength, String source) {
        Objects.requireNonNull(minLength, "`minLength` must be defined.");
        Objects.requireNonNull(maxLength, "`maxLength` must be defined.");
        Objects.requireNonNull(source, "`source` must be defined.");
        if (maxLength < minLength) {
            throw new IllegalArgumentException("`minLength` must be less or equal to `maxLength`.");
        }

        this.minLength = minLength;
        this.range = maxLength - minLength + 1;
        this.source = source.toCharArray();
    }

    @Override
    public String generate() {
        int length = minLength + random.nextInt(range);
        char[] buffer = new char[length];
        for (int i = 0; i < length; i++) {
            buffer[i] = source[random.nextInt(source.length)];
        }

        return String.valueOf(buffer);
    }

}