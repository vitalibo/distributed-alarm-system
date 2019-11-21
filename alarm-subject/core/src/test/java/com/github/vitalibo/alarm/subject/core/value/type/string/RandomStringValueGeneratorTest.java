package com.github.vitalibo.alarm.subject.core.value.type.string;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RandomStringValueGeneratorTest {

    @Test(invocationCount = 100)
    public void testGenerate() {
        RandomStringValueGenerator generator = new RandomStringValueGenerator(
            5, 10, "ABCDEFabcdef");

        String actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.matches("[ABCDEFabcdef]{5,10}"));
    }

    @Test
    public void testGenerateBoundValue() {
        RandomStringValueGenerator generator = new RandomStringValueGenerator(
            5, 5, "ABCDEFabcdef");

        String actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.matches("[ABCDEFabcdef]{5}"));
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`minLength` must be defined.")
    public void testFailCreateMinLengthIsNull() {
        new RandomStringValueGenerator(null, 2, "foo");
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`maxLength` must be defined.")
    public void testFailCreateMaxLengthIsNull() {
        new RandomStringValueGenerator(2, null, "foo");
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`source` must be defined.")
    public void testFailCreateSourceIsNull() {
        new RandomStringValueGenerator(2, 4, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "`minLength` must be less or equal to `maxLength`.")
    public void testFailCreateMaxLengthIsLessThanMaxLength() {
        new RandomStringValueGenerator(4, 3, "foo");
    }

}