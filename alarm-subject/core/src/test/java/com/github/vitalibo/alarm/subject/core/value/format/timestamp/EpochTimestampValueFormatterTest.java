package com.github.vitalibo.alarm.subject.core.value.format.timestamp;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class EpochTimestampValueFormatterTest {

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {ChronoUnit.SECONDS, "2019-01-01T00:00:00Z", 1546300800L},
            {ChronoUnit.MILLIS, "2019-01-01T00:00:00Z", 1546300800000L},
            {ChronoUnit.SECONDS, "2009-02-13T23:31:30Z", 1234567890L},
            {ChronoUnit.MILLIS, "2009-02-13T23:31:30.987Z", 1234567890987L},
        };
    }

    @Test(dataProvider = "samples")
    public void testFormat(ChronoUnit unit, String timestamp, Long expected) {
        EpochTimestampValueFormatter formatter = new EpochTimestampValueFormatter(unit);

        Long actual = formatter.format(
            OffsetDateTime.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, expected);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`unit` must be defined.")
    public void testFailCreateUnitIsNull() {
        new EpochTimestampValueFormatter(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "Supported value.*")
    public void testFailCreateIllegalChronoUnit() {
        new EpochTimestampValueFormatter(ChronoUnit.DAYS);
    }

}