package com.github.vitalibo.alarm.processor.core.util.function;

import com.github.vitalibo.alarm.processor.core.TestHelper;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class ConsumerTest {

    private static AtomicInteger atomicInteger;

    @Spy
    private Consumer<Integer> spyConsumer;

    @BeforeMethod
    public void setUp() {
        atomicInteger = new AtomicInteger();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConsumerSerDe() {
        Consumer<Integer> consumer = (a) -> atomicInteger.set(a * 10);

        Consumer<Integer> actual = TestHelper.serDe(consumer);
        actual.accept(2);

        Assert.assertEquals(atomicInteger.get(), 20);
    }

    @Test
    public void testCall() {
        Mockito.doNothing().when(spyConsumer).accept(Mockito.anyInt());

        spyConsumer.call(1);

        Mockito.verify(spyConsumer).accept(1);
    }

}