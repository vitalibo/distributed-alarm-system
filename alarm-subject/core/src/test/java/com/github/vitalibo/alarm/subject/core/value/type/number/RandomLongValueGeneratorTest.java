package com.github.vitalibo.alarm.subject.core.value.type.number;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RandomLongValueGeneratorTest {

    @Test(invocationCount = 100)
    public void testGenerate() {
        RandomLongValueGenerator generator = new RandomLongValueGenerator(5L, 10L);

        Long actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertTrue(5L <= actual);
        Assert.assertTrue(actual <= 10L);
    }

    @Test
    public void testGenerateBoundValue() {
        RandomLongValueGenerator generator = new RandomLongValueGenerator(5L, 5L);

        Long actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, (Long) 5L);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`minValue` must be defined.")
    public void testFailCreateMinLengthIsNull() {
        new RandomLongValueGenerator(null, 3L);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`maxValue` must be defined.")
    public void testFailCreateMaxLengthIsNull() {
        new RandomLongValueGenerator(4L, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "`minValue` must be less or equal to `maxValue`.")
    public void testFailCreateMaxLengthIsLessThanMaxLength() {
        new RandomLongValueGenerator(4L, 3L);
    }

}