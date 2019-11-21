package com.github.vitalibo.alarm.subject.core.value.type.timestamp;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;

public class RandomTimestampValueGeneratorTest {

    @Test(invocationCount = 100)
    public void testGenerate() {
        OffsetDateTime minValue = OffsetDateTime.parse("2019-01-01T00:00:00Z");
        OffsetDateTime maxValue = OffsetDateTime.parse("2019-01-01T00:00:05Z");
        RandomTimestampValueGenerator generator = new RandomTimestampValueGenerator(minValue, maxValue);

        OffsetDateTime actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isAfter(minValue) || actual.isEqual(minValue));
        Assert.assertTrue(actual.isBefore(maxValue) || actual.isEqual(maxValue));
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`minValue` must be defined.")
    public void testFailCreateMinValueIsNull() {
        new RandomTimestampValueGenerator(null, OffsetDateTime.now());
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`maxValue` must be defined.")
    public void testFailCreateMaxValueIsNull() {
        new RandomTimestampValueGenerator(OffsetDateTime.now(), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "`minValue` must be less or equal to `maxValue`.")
    public void testFailCreateMaxValueIsLessThanMaxValue() {
        OffsetDateTime now = OffsetDateTime.now();
        new RandomTimestampValueGenerator(now.plusHours(1), now);
    }

}