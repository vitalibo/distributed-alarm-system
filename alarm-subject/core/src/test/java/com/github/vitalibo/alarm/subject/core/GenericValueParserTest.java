package com.github.vitalibo.alarm.subject.core;

import com.github.vitalibo.alarm.subject.core.value.format.CsvValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.format.JsonValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.format.number.PatternNumberValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.format.number.PrecisionDoubleValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.format.timestamp.EpochTimestampValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.format.timestamp.PatternTimestampValueFormatter;
import com.github.vitalibo.alarm.subject.core.value.type.SchemaValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.number.*;
import com.github.vitalibo.alarm.subject.core.value.type.string.FixedStringValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.string.RandomStringValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.string.UUIDStringValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.timestamp.CurrentTimestampValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.timestamp.FixedTimestampValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.timestamp.NaturalGrowthTimestampValueGenerator;
import com.github.vitalibo.alarm.subject.core.value.type.timestamp.RandomTimestampValueGenerator;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class GenericValueParserTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testParseSchemaObject() {
        Config config = ConfigFactory.load("object/object.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof SchemaValueGenerator);
        Map<String, Object> value = (Map<String, Object>) actual.get();
        Assert.assertTrue(value.containsKey("bar"));
        Assert.assertEquals(value.get("bar"), 12345L);
        Assert.assertTrue(value.containsKey("foo"));
        Assert.assertEquals(value.get("foo"), "foo52");
        Assert.assertTrue(value.containsKey("baz"));
        Assert.assertEquals(value.get("baz"), 1546300800L);
        Assert.assertTrue(value.containsKey("tar"));
        Map<String, Object> inner = (Map<String, Object>) value.get("tar");
        Assert.assertTrue(inner.containsKey("taz"));
        Assert.assertEquals(inner.get("taz"), "inner");
        Assert.assertEquals(inner.size(), 1);
        Assert.assertEquals(value.size(), 4);
    }

    @Test
    public void testParseSchemaCsv() {
        Config config = ConfigFactory.load("object/object|csv.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof SchemaValueGenerator);
        Assert.assertTrue(actual.getFormatter() instanceof CsvValueFormatter);
        Assert.assertEquals(actual.get(), "1.2345,foo52,2019-01-01T00:00:00Z");
    }

    @Test
    public void testParseSchemaJson() {
        Config config = ConfigFactory.load("object/object|json.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof SchemaValueGenerator);
        Assert.assertTrue(actual.getFormatter() instanceof JsonValueFormatter);
        Assert.assertEquals(actual.get(),
            "{\"bar\":12345,\"tar\":{\"taz\":\"inner\"},\"foo\":\"foo52\",\"baz\":1546300800}");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = ".*type.*")
    public void testParseUnsupportedType() {
        Config config = ConfigFactory.load("object/foo.conf");

        GenericValueParser.parse(config);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = ".*format.*")
    public void testParseSchemaUnsupportedFormat() {
        Config config = ConfigFactory.load("object/object|foo.conf");

        GenericValueParser.parse(config);
    }

    @Test
    public void testParseNumberFixedLong() {
        Config config = ConfigFactory.load("number/long.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof FixedLongValueGenerator);
        Assert.assertEquals(actual.get(), 123L);
    }

    @Test
    public void testParseNumberFixedLongPattern() {
        Config config = ConfigFactory.load("number/long|pattern.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof FixedLongValueGenerator);
        Assert.assertTrue(actual.getFormatter() instanceof PatternNumberValueFormatter);
        Assert.assertEquals(actual.get(), "0123");
    }

    @Test
    public void testParseNumberRandomLong() {
        Config config = ConfigFactory.load("number/randomLong.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof RandomLongValueGenerator);
        Long value = (Long) actual.get();
        Assert.assertTrue(value >= 10);
        Assert.assertTrue(value <= 12);
    }

    @Test
    public void testParseNumberIncrement() {
        Config config = ConfigFactory.load("number/increment.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof IncrementalValueGenerator);
        Assert.assertEquals(actual.get(), 10L);
        Assert.assertEquals(actual.get(), 12L);
    }

    @Test
    public void testParseNumberFixedDouble() {
        Config config = ConfigFactory.load("number/double.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof FixedDoubleValueGenerator);
        Assert.assertEquals(actual.get(), 1.234567);
    }

    @Test
    public void testParseNumberFixedDoublePattern() {
        Config config = ConfigFactory.load("number/double|pattern.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof FixedDoubleValueGenerator);
        Assert.assertTrue(actual.getFormatter() instanceof PatternNumberValueFormatter);
        Assert.assertEquals(actual.get(), "1.235");
    }

    @Test
    public void testParseNumberFixedDoublePrecision() {
        Config config = ConfigFactory.load("number/double|precision.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof FixedDoubleValueGenerator);
        Assert.assertTrue(actual.getFormatter() instanceof PrecisionDoubleValueFormatter);
        Assert.assertEquals(actual.get(), 1.234);
    }

    @Test
    public void testParseNumberRandomDouble() {
        Config config = ConfigFactory.load("number/randomDouble.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof RandomDoubleValueGenerator);
        Double value = (Double) actual.get();
        Assert.assertTrue(value >= 10.5);
        Assert.assertTrue(value <= 11.5);
    }

    @Test
    public void testParseNumberFunction() {
        Config config = ConfigFactory.load("number/function.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof FunctionDoubleValueGenerator);
        Assert.assertEquals(actual.get(), 1.0);
        Assert.assertTrue(((double) actual.get() - 0.96) < 0.01);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = ".*type.*")
    public void testParseNumberUnsupportedType() {
        Config config = ConfigFactory.load("number/foo.conf");

        GenericValueParser.parse(config);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = ".*format.*")
    public void testParseNumberUnsupportedFormat() {
        Config config = ConfigFactory.load("number/long|foo.conf");

        GenericValueParser.parse(config);
    }

    @Test
    public void testParseStringFixedString() {
        Config config = ConfigFactory.load("string/string.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof FixedStringValueGenerator);
        Assert.assertEquals(actual.get(), "foo");
    }

    @Test
    public void testParseStringRandomString() {
        Config config = ConfigFactory.load("string/random.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof RandomStringValueGenerator);
        String value = (String) actual.get();
        Assert.assertTrue(value.matches("[ABCDabcd]{10,15}"));
    }

    @Test
    public void testParseStringUUID() {
        Config config = ConfigFactory.load("string/uuid.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof UUIDStringValueGenerator);
        Assert.assertNotNull(actual.get());
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = ".*type.*")
    public void testParseStringUnsupportedType() {
        Config config = ConfigFactory.load("string/foo.conf");

        GenericValueParser.parse(config);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = ".*format.*")
    public void testParseStringUnsupportedFormat() {
        Config config = ConfigFactory.load("string/string|foo.conf");

        GenericValueParser.parse(config);
    }

    @Test
    public void testParseTimestampFixedTimestamp() {
        Config config = ConfigFactory.load("timestamp/timestamp.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof FixedTimestampValueGenerator);
        Assert.assertTrue(actual.getFormatter() instanceof EpochTimestampValueFormatter);
        Assert.assertEquals(actual.get(), 1546300800L);
    }

    @Test
    public void testParseTimestampFixedTimestampSeconds() {
        Config config = ConfigFactory.load("timestamp/timestamp|epoch|seconds.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof FixedTimestampValueGenerator);
        Assert.assertTrue(actual.getFormatter() instanceof EpochTimestampValueFormatter);
        Assert.assertEquals(actual.get(), 1546300800L);
    }

    @Test
    public void testParseTimestampFixedTimestampMilliseconds() {
        Config config = ConfigFactory.load("timestamp/timestamp|epoch|milliseconds.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof FixedTimestampValueGenerator);
        Assert.assertTrue(actual.getFormatter() instanceof EpochTimestampValueFormatter);
        Assert.assertEquals(actual.get(), 1546300800000L);
    }

    @Test
    public void testParseTimestampFixedTimestampPattern() {
        Config config = ConfigFactory.load("timestamp/timestamp|pattern.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof FixedTimestampValueGenerator);
        Assert.assertTrue(actual.getFormatter() instanceof PatternTimestampValueFormatter);
        Assert.assertEquals(actual.get(), "Tue, 1 Jan 2019 00:00:00 GMT");
    }

    @Test
    public void testParseTimestampNow() {
        Config config = ConfigFactory.load("timestamp/now.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof CurrentTimestampValueGenerator);
        Assert.assertNotNull(actual.get());
    }

    @Test
    public void testParseTimestampRandom() {
        Config config = ConfigFactory.load("timestamp/random.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof RandomTimestampValueGenerator);
        Long value = (Long) actual.get();
        Assert.assertTrue(value >= 1546300800);
        Assert.assertTrue(value <= 1546300860);
    }

    @Test
    public void testParseTimestampNarrowGrowth() {
        Config config = ConfigFactory.load("timestamp/naturalGrowth.conf");

        GenericValue actual = GenericValueParser.parse(config);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getGenerator() instanceof NaturalGrowthTimestampValueGenerator);
        Assert.assertTrue((Long) actual.get() >= 1546300800);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = ".*type.*")
    public void testParseTimestampUnsupportedType() {
        Config config = ConfigFactory.load("timestamp/foo.conf");

        GenericValueParser.parse(config);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = ".*format.*")
    public void testParseTimestampUnsupportedFormat() {
        Config config = ConfigFactory.load("timestamp/timestamp|foo.conf");

        GenericValueParser.parse(config);
    }

}