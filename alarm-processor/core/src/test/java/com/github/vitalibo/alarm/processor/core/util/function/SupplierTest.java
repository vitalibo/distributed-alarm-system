package com.github.vitalibo.alarm.processor.core.util.function;

import com.github.vitalibo.alarm.processor.core.TestHelper;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SupplierTest {

    @Spy
    private Supplier<Integer> spySupplier;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSupplierSerDe() {
        Supplier<Integer> supplier = "foo"::length;

        Supplier<Integer> actual = TestHelper.serDe(supplier);

        Assert.assertEquals(actual.get(), (Integer) 3);
    }

    @Test
    public void testCall() {
        Mockito.doReturn(1).when(spySupplier).get();

        Integer actual = spySupplier.call();

        Assert.assertEquals(actual, (Integer) 1);
        Mockito.verify(spySupplier).get();
    }

}