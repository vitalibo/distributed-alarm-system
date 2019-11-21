package com.github.vitalibo.alarm.subject.core.value.type.string;

import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.util.UUID;

public class UUIDStringValueGenerator implements ValueGenerator<String> {

    @Override
    public String generate() {
        return String.valueOf(UUID.randomUUID());
    }

}