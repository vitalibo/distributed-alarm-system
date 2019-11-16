package com.github.vitalibo.alarm.processor.core.util.function;

import com.github.vitalibo.alarm.processor.core.TestHelper;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BiFunctionTest {

    @Spy
    private BiFunction<String, String, String> spyBiFunction;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBiFunctionSerDe() {
        BiFunction<String, String, String> biFunction = String::concat;

        BiFunction<String, String, String> actual = TestHelper.serDe(biFunction);

        Assert.assertEquals(actual.apply("foo", "bar"), "foobar");
    }

    @Test
    public void testCall() {
        Mockito.doReturn("baz").when(spyBiFunction).apply(Mockito.anyString(), Mockito.anyString());

        String actual = spyBiFunction.call("foo", "bar");

        Assert.assertEquals(actual, "baz");
        Mockito.verify(spyBiFunction).apply("foo", "bar");
    }

}