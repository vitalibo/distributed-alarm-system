package com.github.vitalibo.alarm.subject.core.value.type.timestamp;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;

public class FixedTimestampValueGeneratorTest {

    @Test
    public void testGenerate() {
        OffsetDateTime expected = OffsetDateTime.parse("2019-01-01T00:00:00Z");
        FixedTimestampValueGenerator generator = new FixedTimestampValueGenerator(expected);

        OffsetDateTime actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, expected);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`value` must be defined.")
    public void testFailCreateValueIsNull() {
        new FixedTimestampValueGenerator(null);
    }

}