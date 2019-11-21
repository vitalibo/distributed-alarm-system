package com.github.vitalibo.alarm.subject.core.value.type.number;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RandomDoubleValueGeneratorTest {

    @Test(invocationCount = 100)
    public void testGenerate() {
        RandomDoubleValueGenerator generator = new RandomDoubleValueGenerator(5.5, 9.5);

        Double actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertTrue(5.5 <= actual);
        Assert.assertTrue(actual <= 9.5);
    }

    @Test
    public void testGenerateBoundValue() {
        RandomDoubleValueGenerator generator = new RandomDoubleValueGenerator(5.5, 5.5);

        Double actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, 5.5);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`minValue` must be defined.")
    public void testFailCreateMinLengthIsNull() {
        new RandomDoubleValueGenerator(null, 3.);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`maxValue` must be defined.")
    public void testFailCreateMaxLengthIsNull() {
        new RandomDoubleValueGenerator(4., null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "`minValue` must be less or equal to `maxValue`.")
    public void testFailCreateMaxLengthIsLessThanMaxLength() {
        new RandomDoubleValueGenerator(4., 3.);
    }

}