package com.github.vitalibo.alarm.subject.core.value.type.number;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IncrementalValueGeneratorTest {

    @Test
    public void testGenerate() {
        IncrementalValueGenerator generator = new IncrementalValueGenerator(0L, 1);

        Long actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, (Long) 0L);

        actual = generator.generate();

        Assert.assertEquals(actual, (Long) 1L);
    }

    @Test
    public void testGenerateDecrement() {
        IncrementalValueGenerator generator = new IncrementalValueGenerator(0L, -1);

        Long actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, (Long) 0L);

        actual = generator.generate();

        Assert.assertEquals(actual, new Long(-1));
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`startValue` must be defined.")
    public void testFailCreateStartValueIsNull() {
        new IncrementalValueGenerator(null, 1);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`step` must be defined.")
    public void testFailCreateStepIsNull() {
        new IncrementalValueGenerator(1L, null);
    }

}