package com.github.vitalibo.alarm.subject.core;

import com.github.vitalibo.alarm.subject.core.value.ValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.ValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.format.CsvValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.format.JsonValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.format.number.PatternNumberValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.format.number.PrecisionDoubleValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.format.timestamp.EpochTimestampValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.format.timestamp.PatternTimestampValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.type.SchemaValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.SchemaValueGenerator.Schema;
import com.github.vitalibo.alarm.subject.core.value.type.number.*;
import com.github.vitalibo.alarm.subject.core.value.type.string.FixedStringValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.string.RandomStringValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.string.UUIDStringValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.timestamp.CurrentTimestampValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.timestamp.FixedTimestampValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.timestamp.NaturalGrowthTimestampValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.timestamp.RandomTimestampValueGenerator;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public final class GenericValueParser {

    private GenericValueParser() {
    }

    @SuppressWarnings("unchecked")
    public static <K, V> GenericValue<K, V> parse(Config config) {
        return parse(new ConfigDecorator(config));
    }

    @SuppressWarnings("unchecked")
    private static GenericValue parse(ConfigDecorator config) {
        String type = config.getString("type");

        switch (type.split("\\|")[0]) {
            case "object":
                return new GenericValue<>(
                    parseSchemaValueGenerator(config),
                    parseSchemaValueFormatter(config));
            case "string":
                return new GenericValue<>(
                    parseStringValueGenerator(config),
                    parseStringValueFormatter(config));
            case "timestamp":
                return new GenericValue<>(
                    parseDateTimeValueGenerator(config),
                    parseDateTimeValueFormatter(config));
            case "number":
                return new GenericValue(
                    parseNumberValueGenerator(config),
                    parseNumberValueFormatter(config));
            default:
                throw new IllegalArgumentException(
                    String.format("Unsupported type: %s", type));
        }
    }

    private static ValueGenerator<Map<String, ?>> parseSchemaValueGenerator(ConfigDecorator config) {
        final Schema schema = new Schema();
        for (ConfigObject item : config.getObjectList("schema")) {
            ConfigDecorator field = new ConfigDecorator(item.toConfig());
            schema.put(field.getString("name"), parse(field));
        }

        return new SchemaValueGenerator(schema);
    }

    private static ValueFormatter<Map<String, ?>, ?> parseSchemaValueFormatter(ConfigDecorator config) {
        String format = config.getString("format", "default");

        switch (format) {
            case "csv":
                return new CsvValueFormatter();
            case "json":
                return new JsonValueFormatter();
            case "default":
                return o -> o;
            default:
                throw new IllegalArgumentException(
                    String.format("Unsupported format: %s", format));
        }
    }

    private static ValueGenerator<String> parseStringValueGenerator(ConfigDecorator config) {
        String type = config.getString("type");

        switch (type) {
            case "string":
                return new FixedStringValueGenerator(
                    config.getString("value", null));
            case "string|random":
                Integer minLength = config.getInt("minLength", 24);
                return new RandomStringValueGenerator(
                    minLength,
                    config.getInt("maxLength", minLength),
                    config.getString("source",
                        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789_-"));
            case "string|uuid":
                return new UUIDStringValueGenerator();
            default:
                throw new IllegalArgumentException(
                    String.format("Unsupported type: %s", type));
        }
    }

    private static ValueFormatter<String, ?> parseStringValueFormatter(ConfigDecorator config) {
        String format = config.getString("format", "default");

        switch (format) {
            case "default":
                return o -> o;
            default:
                throw new IllegalArgumentException(
                    String.format("Unsupported format: %s", format));
        }
    }

    private static ValueGenerator<OffsetDateTime> parseDateTimeValueGenerator(ConfigDecorator config) {
        String type = config.getString("type");

        switch (type) {
            case "timestamp":
                return new FixedTimestampValueGenerator(
                    config.getOffsetDateTime("value", OffsetDateTime.now(ZoneId.of("UTC"))));
            case "timestamp|now":
                return new CurrentTimestampValueGenerator();
            case "timestamp|random":
                return new RandomTimestampValueGenerator(
                    config.getOffsetDateTime("minValue", null),
                    config.getOffsetDateTime("maxValue", null));
            case "timestamp|naturalGrowth":
                return new NaturalGrowthTimestampValueGenerator(
                    config.getOffsetDateTime("startAt", null));
            default:
                throw new IllegalArgumentException(
                    String.format("Unsupported type: %s", type));
        }
    }

    private static ValueFormatter<OffsetDateTime, ?> parseDateTimeValueFormatter(ConfigDecorator config) {
        String format = config.getString("format", "epoch");

        switch (format) {
            case "epoch":
            case "epoch|seconds":
                return new EpochTimestampValueFormatter(ChronoUnit.SECONDS);
            case "epoch|milliseconds":
                return new EpochTimestampValueFormatter(ChronoUnit.MILLIS);
            default:
                if (format.startsWith("pattern|")) {
                    return new PatternTimestampValueFormatter(
                        format.split("\\|")[1]);
                }

                throw new IllegalArgumentException(
                    String.format("Unsupported format: %s", format));
        }
    }

    private static ValueGenerator<? extends Number> parseNumberValueGenerator(ConfigDecorator config) {
        String type = config.getString("type");

        switch (type) {
            case "number|long":
                return new FixedLongValueGenerator(
                    config.getLong("value", null));
            case "number|randomLong":
                return new RandomLongValueGenerator(
                    config.getLong("minValue", 0L),
                    config.getLong("maxValue", (long) Integer.MAX_VALUE));
            case "number|increment":
                return new IncrementalValueGenerator(
                    config.getLong("startValue", 0L),
                    config.getInt("step", 1));
            case "number|double":
                return new FixedDoubleValueGenerator(
                    config.getDouble("value", null));
            case "number|randomDouble":
                return new RandomDoubleValueGenerator(
                    config.getDouble("minValue", 0.0),
                    config.getDouble("maxValue", 1.0));
            case "number|function":
                return new FunctionDoubleValueGenerator(
                    config.getString("function", "sin(2X)-2sin(X)"),
                    config.getDouble("startValue", 0.0),
                    config.getDouble("step", 1.0));
            default:
                throw new IllegalArgumentException(
                    String.format("Unsupported type: %s", type));
        }
    }

    private static ValueFormatter<Number, ?> parseNumberValueFormatter(ConfigDecorator config) {
        String format = config.getString("format", "default");

        if ("default".equals(format)) {
            return o -> o;
        }

        if (format.startsWith("precision|")) {
            return new PrecisionDoubleValueFormatter(
                format.split("\\|")[1]);
        }

        if (format.startsWith("pattern|")) {
            return new PatternNumberValueFormatter(
                format.split("\\|")[1]);
        }

        throw new IllegalArgumentException(
            String.format("Unsupported format: %s", format));
    }

    @RequiredArgsConstructor
    private static class ConfigDecorator implements Config {

        @Delegate
        private final Config delegate;

        Integer getInt(String path, Integer defaultValue) {
            return delegate.hasPath(path) ? delegate.getInt(path) : defaultValue;
        }

        Long getLong(String path, Long defaultValue) {
            return delegate.hasPath(path) ? delegate.getLong(path) : defaultValue;
        }

        Double getDouble(String path, Double defaultValue) {
            return delegate.hasPath(path) ? delegate.getDouble(path) : defaultValue;
        }

        String getString(String path, String defaultValue) {
            return delegate.hasPath(path) ? delegate.getString(path) : defaultValue;
        }

        OffsetDateTime getOffsetDateTime(String path, OffsetDateTime defaultValue) {
            if (!delegate.hasPath(path)) {
                return defaultValue;
            }

            final String value = delegate.getString(path);
            if (value.matches("[0-9]+")) {
                return Instant.ofEpochSecond(Long.parseLong(value))
                    .atOffset(ZoneOffset.UTC);
            }

            return OffsetDateTime.parse(value);
        }

    }

}