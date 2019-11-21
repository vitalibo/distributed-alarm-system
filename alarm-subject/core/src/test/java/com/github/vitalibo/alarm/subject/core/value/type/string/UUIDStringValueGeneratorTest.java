package com.github.vitalibo.alarm.subject.core.value.type.string;

import org.testng.Assert;
import org.testng.annotations.Test;

public class UUIDStringValueGeneratorTest {

    @Test
    public void testGenerate() {
        UUIDStringValueGenerator generator = new UUIDStringValueGenerator();

        String actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.matches(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"));
    }

}