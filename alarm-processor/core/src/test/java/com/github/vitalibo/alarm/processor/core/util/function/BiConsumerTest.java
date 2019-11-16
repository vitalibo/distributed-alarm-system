package com.github.vitalibo.alarm.processor.core.util.function;

import com.github.vitalibo.alarm.processor.core.TestHelper;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class BiConsumerTest {

    private static AtomicInteger atomicInteger;

    @Spy
    private BiConsumer<Integer, Integer> spyBiConsumer;

    @BeforeMethod
    public void setUp() {
        atomicInteger = new AtomicInteger();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBiConsumerSerDe() {
        BiConsumer<Integer, Integer> biConsumer = (a, b) -> atomicInteger.set(a + b);

        BiConsumer<Integer, Integer> actual = TestHelper.serDe(biConsumer);
        actual.accept(1, 2);

        Assert.assertEquals(atomicInteger.get(), 3);
    }

    @Test
    public void testCall() {
        Mockito.doNothing().when(spyBiConsumer).accept(Mockito.anyInt(), Mockito.anyInt());

        spyBiConsumer.call(1, 2);

        Mockito.verify(spyBiConsumer).accept(1, 2);
    }

}