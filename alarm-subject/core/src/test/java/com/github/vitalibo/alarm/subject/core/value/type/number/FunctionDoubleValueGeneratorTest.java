package com.github.vitalibo.alarm.subject.core.value.type.number;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionDoubleValueGeneratorTest {

    @Test
    public void testGenerate() {
        FunctionDoubleValueGenerator generator = new FunctionDoubleValueGenerator(o -> o * 1.1, 0.0, 1.0);

        Double actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, 0.0);

        actual = generator.generate();
        Assert.assertEquals(actual, 1.1);
    }

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {"sin(X)", Arrays.asList(
                0.0, 0.25, 0.49, 0.7, 0.86, 0.96, 1.0, 0.96, 0.86, 0.7, 0.49, 0.25,
                0.0, -0.25, -0.5, -0.7, -0.86, -0.96, -1.0, -0.96, -0.86, -0.7, -0.5, -0.25)},
            {"cos(X)", Arrays.asList(
                1.0, 0.96, 0.86, 0.7, 0.5, 0.25, 0.0, -0.25, -0.49, -0.7, -0.86, -0.96,
                -1.0, -0.96, -0.86, -0.7, -0.5, -0.25, 0.0, 0.25, 0.5, 0.7, 0.86, 0.96)},
            {"sin(2X)-2sin(X)", Arrays.asList(
                0.0, -0.01, -0.13, -0.41, -0.86, -1.43, -1.99, -2.43, -2.59, -2.41, -1.86, -1.01,
                0.0, 1.01, 1.86, 2.41, 2.59, 2.43, 2.0, 1.43, 0.86, 0.41, 0.13, 0.01)}
        };
    }

    @Test(dataProvider = "samples")
    public void testGenerateSin(String fn, List<Double> expected) {
        FunctionDoubleValueGenerator generator = new FunctionDoubleValueGenerator(fn, 0.0, 15.0);

        List<Double> actual = Stream.generate(generator::generate)
            .limit(24)
            .map(o -> (int) (o * 100.) / 100.)
            .collect(Collectors.toList());

        Assert.assertEquals(actual, expected);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`function` must be defined.")
    public void testFailCreateFunctionIsNull() {
        new FunctionDoubleValueGenerator((String) null, 0.0, 1.0);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`startValue` must be defined.")
    public void testFailCreateStartValueIsNull() {
        new FunctionDoubleValueGenerator("sin(X)", null, 1.0);
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`step` must be defined.")
    public void testFailCreateStepIsNull() {
        new FunctionDoubleValueGenerator("sin(X)", 0.0, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "Unknown formula.")
    public void testFailCreateUnknownFunction() {
        new FunctionDoubleValueGenerator("tan(X)", 0.0, 1.0);
    }

}