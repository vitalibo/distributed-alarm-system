package com.github.vitalibo.alarm.subject.core.value.format;

import com.github.vitalibo.alarm.subject.core.value.ValueFormatter;

import java.util.Map;
import java.util.stream.Collectors;

public class CsvValueFormatter implements ValueFormatter<Map<String, ?>, String> {

    @Override
    public String format(Map<String, ?> object) {
        return object.values()
            .stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
    }

}