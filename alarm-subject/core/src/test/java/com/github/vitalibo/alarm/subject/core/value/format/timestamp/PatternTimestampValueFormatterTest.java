package com.github.vitalibo.alarm.subject.core.value.format.timestamp;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class PatternTimestampValueFormatterTest {

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {"ISO_OFFSET_DATE_TIME", "2019-01-01T00:00:00+02:00", "2019-01-01T00:00:00+02:00"},
            {"ISO_OFFSET_DATE", "2019-01-01T00:00:00+02:00", "2019-01-01+02:00"},
            {"ISO_OFFSET_TIME", "2019-01-01T12:34:56+02:00", "12:34:56+02:00"},
            {"RFC_1123_DATE_TIME", "2019-01-01T00:00:00+02:00", "Tue, 1 Jan 2019 00:00:00 +0200"}
        };
    }

    @Test(dataProvider = "samples")
    public void testFormat(String pattern, String timestamp, String expected) {
        PatternTimestampValueFormatter formatter = new PatternTimestampValueFormatter(pattern);

        String actual = formatter.format(
            OffsetDateTime.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, expected);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`pattern` must be defined.")
    public void testFailCreatePatternIsNull() {
        new PatternTimestampValueFormatter(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "Unknown pattern.*")
    public void testFailCreateUnsupportedPattern() {
        new PatternTimestampValueFormatter("foo");
    }

}