package com.github.vitalibo.alarm.subject.core.value.format.number;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PrecisionDoubleValueFormatterTest {

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {"0.1", 1.234567, 1.2},
            {"0.01", 1.234567, 1.23},
            {"0.001", 1.234567, 1.234},
            {"0.0001", 1.234567, 1.2345},
            {"0.00001", 1.234567, 1.23456},
            {"0.000001", 1.234567, 1.234567},
            {"0.0000001", 1.234567, 1.234567}
        };
    }

    @Test(dataProvider = "samples")
    public void testFormat(String precision, Double value, Double expected) {
        PrecisionDoubleValueFormatter formatter = new PrecisionDoubleValueFormatter(precision);

        Double actual = formatter.format(value);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, expected);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`precision` must be defined.")
    public void testFailCreatePrecisionIsNull() {
        new PrecisionDoubleValueFormatter(null);
    }

    @DataProvider
    public Object[][] samplesIncorrectPrecision() {
        return new Object[][]{
            {"1"}, {".1"}, {"00.1"}, {"-0.1"}, {"0.2"}, {"0.02"}
        };
    }

    @Test(dataProvider = "samplesIncorrectPrecision",
        expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "`precision` not matches pattern.")
    public void testFailCreateIncorrectPrecision(String precision) {
        new PrecisionDoubleValueFormatter(precision);
    }

}