package com.github.vitalibo.alarm.subject.core.value.type;

import com.github.vitalibo.alarm.subject.core.GenericValue;
import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SchemaValueGenerator implements ValueGenerator<Map<String, ?>> {

    private final Schema schema;

    public SchemaValueGenerator(Schema schema) {
        Objects.requireNonNull(schema, "`schema` must be defined.");
        this.schema = schema;
    }

    @Override
    public Map<String, ?> generate() {
        return schema.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey, entry -> entry.getValue().get()));
    }

    public static class Schema extends HashMap<String, GenericValue> {
    }

}