package com.github.vitalibo.alarm.subject.core.value.type.timestamp;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;

public class CurrentTimestampValueGeneratorTest {

    @Test
    public void testGenerate() {
        CurrentTimestampValueGenerator generator = new CurrentTimestampValueGenerator();
        OffsetDateTime before = OffsetDateTime.now();

        OffsetDateTime actual = generator.get();

        OffsetDateTime after = OffsetDateTime.now();
        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isAfter(before) || actual.isEqual(before));
        Assert.assertTrue(actual.isBefore(after) || actual.isEqual(after));
    }

}