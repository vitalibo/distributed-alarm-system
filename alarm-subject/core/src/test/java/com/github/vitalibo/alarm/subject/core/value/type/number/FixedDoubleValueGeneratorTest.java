package com.github.vitalibo.alarm.subject.core.value.type.number;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FixedDoubleValueGeneratorTest {

    @Test
    public void testGenerate() {
        FixedDoubleValueGenerator generator = new FixedDoubleValueGenerator(1.2);

        Double actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, 1.2);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`value` must be defined.")
    public void testFailCreateValueIsNull() {
        new FixedDoubleValueGenerator(null);
    }

}