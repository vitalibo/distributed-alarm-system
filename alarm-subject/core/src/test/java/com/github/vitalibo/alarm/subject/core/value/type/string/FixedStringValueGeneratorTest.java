package com.github.vitalibo.alarm.subject.core.value.type.string;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FixedStringValueGeneratorTest {

    @Test
    public void testGenerate() {
        FixedStringValueGenerator generator = new FixedStringValueGenerator("foo");

        String actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "foo");
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`value` must be defined.")
    public void testFailCreateValueIsNull() {
        new FixedStringValueGenerator(null);
    }

}