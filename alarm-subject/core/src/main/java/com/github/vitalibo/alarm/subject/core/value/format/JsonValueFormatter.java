package com.github.vitalibo.alarm.subject.core.value.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vitalibo.alarm.subject.core.value.ValueFormatter;
import lombok.SneakyThrows;

import java.util.Map;

public class JsonValueFormatter implements ValueFormatter<Map<String, ?>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SneakyThrows
    public String format(Map<String, ?> object) {
        return objectMapper.writeValueAsString(object);
    }

}