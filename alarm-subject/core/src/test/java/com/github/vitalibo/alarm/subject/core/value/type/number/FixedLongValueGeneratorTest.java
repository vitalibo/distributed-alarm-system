package com.github.vitalibo.alarm.subject.core.value.type.number;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FixedLongValueGeneratorTest {

    @Test
    public void testGenerate() {
        FixedLongValueGenerator generator = new FixedLongValueGenerator(12L);

        Long actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, (Long) 12L);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`value` must be defined.")
    public void testFailCreateValueIsNull() {
        new FixedLongValueGenerator(null);
    }

}