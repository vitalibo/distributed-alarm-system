package com.github.vitalibo.alarm.subject.core.value.format.number;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PatternNumberValueFormatterTest {

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {"%d", 123, "123"}, {"%04d", 123, "0123"}, {"%+4d", 12, " +12"}, {"%,4d", 1234, "1,234"},
            {"%f", 1.23456, "1.234560"}, {"%.2f", 1.23456, "1.23"}, {"%5.2f", 1.23456, " 1.23"}
        };
    }

    @Test(dataProvider = "samples")
    public void testFormat(String pattern, Number number, String expected) {
        PatternNumberValueFormatter formatter = new PatternNumberValueFormatter(pattern);

        String actual = formatter.format(number);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, expected);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`pattern` must be defined.")
    public void testFailCreatePatternIsNull() {
        new PatternNumberValueFormatter(null);
    }

}