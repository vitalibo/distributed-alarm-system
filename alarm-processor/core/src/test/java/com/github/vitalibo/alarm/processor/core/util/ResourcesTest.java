package com.github.vitalibo.alarm.processor.core.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStream;

public class ResourcesTest {

    @Test
    public void testAsString() {
        String actual = Resources.asString("/Alarm.json");

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.length() > 0);
    }

    @Test
    public void testAsBufferedReader() {
        BufferedReader actual = Resources.asBufferedReader("/Metric.json");

        Assert.assertNotNull(actual);
    }

    @Test
    public void testAsInputStream() {
        InputStream actual = Resources.asInputStream("/Rule.json");

        Assert.assertNotNull(actual);
    }

}