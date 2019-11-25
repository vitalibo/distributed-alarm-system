package com.github.vitalibo.alarm.api.core.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStream;

public class ResourcesTest {

    @Test
    public void testAsString() {
        String actual = Resources.asString("/CreateRuleRequest.json");

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.length() > 0);
    }

    @Test
    public void testAsBufferedReader() {
        BufferedReader actual = Resources.asBufferedReader("/HttpError.json");

        Assert.assertNotNull(actual);
    }

    @Test
    public void testAsInputStream() {
        InputStream actual = Resources.asInputStream("/UpdateRuleRequest.json");

        Assert.assertNotNull(actual);
    }

}