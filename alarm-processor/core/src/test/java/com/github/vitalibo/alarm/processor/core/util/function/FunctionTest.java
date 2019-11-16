package com.github.vitalibo.alarm.processor.core.util.function;

import com.github.vitalibo.alarm.processor.core.TestHelper;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FunctionTest {

    @Spy
    private Function<Integer, String> spyFunction;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFunctionSerDe() {
        Function<Integer, String> function = String::valueOf;

        Function<Integer, String> actual = TestHelper.serDe(function);

        Assert.assertEquals(actual.apply(1), "1");
    }

    @Test
    public void testCall() {
        Mockito.doReturn("1").when(spyFunction).apply(Mockito.anyInt());

        String actual = spyFunction.call(1);

        Assert.assertEquals(actual, "1");
        Mockito.verify(spyFunction).apply(1);
    }

}