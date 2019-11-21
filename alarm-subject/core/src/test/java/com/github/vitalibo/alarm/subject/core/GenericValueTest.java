package com.github.vitalibo.alarm.subject.core;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GenericValueTest {

    @Test
    public void testGet() {
        GenericValue<String, String> value = new GenericValue<>(() -> "foo", String::toUpperCase);

        String actual = value.get();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "FOO");
    }

}