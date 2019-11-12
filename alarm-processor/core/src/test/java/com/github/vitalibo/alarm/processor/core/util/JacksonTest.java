package com.github.vitalibo.alarm.processor.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

public class JacksonTest {

    @Test
    public void testToJsonString() {
        String actual = Jackson.toJsonString(
            Collections.singletonMap("foo", "bar"));

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "{\"foo\":\"bar\"}");
    }

    @Test
    public void testFromJsonString() {
        String jsonString = "{\"foo\":\"bar\"}";

        Map actual = Jackson.fromJsonString(jsonString, Map.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }

    @Test
    public void testFromJsonBytes() {
        byte[] bytes = "{\"foo\":\"bar\"}".getBytes();

        Map actual = Jackson.fromJsonString(bytes, Map.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }

    @Test
    public void testFromJsonInputStream() {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"foo\":\"bar\"}".getBytes());

        Map actual = Jackson.fromJsonString(stream, Map.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }

    @Test
    public void testFromJsonStringTypeReference() {
        String jsonString = "{\"foo\":\"bar\"}";

        Map<String, String> actual = Jackson.fromJsonString(jsonString, new Type());

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }

    @Test
    public void testFromJsonBytesTypeReference() {
        byte[] bytes = "{\"foo\":\"bar\"}".getBytes();

        Map<String, String> actual = Jackson.fromJsonString(bytes, new Type());

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }

    @Test
    public void testFromJsonInputStreamTypeReference() {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"foo\":\"bar\"}".getBytes());

        Map<String, String> actual = Jackson.fromJsonString(stream, new Type());

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), "bar");
    }

    @Test
    public void testParseOffsetDateTime() {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"foo\":1564742802}".getBytes());

        Map<String, OffsetDateTime> actual = Jackson.fromJsonString(stream, new TypeOffsetDateTime());

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get("foo"), OffsetDateTime.parse(
            "2019-08-02T10:46:42Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    private static class Type extends TypeReference<Map<String, String>> {
    }

    private static class TypeOffsetDateTime extends TypeReference<Map<String, OffsetDateTime>> {}
}