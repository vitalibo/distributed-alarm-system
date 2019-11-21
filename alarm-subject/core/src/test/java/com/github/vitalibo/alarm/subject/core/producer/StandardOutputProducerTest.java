package com.github.vitalibo.alarm.subject.core.producer;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.PrintStream;

public class StandardOutputProducerTest {

    @Mock
    private PrintStream mockPrintStream;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSend() {
        StandardOutputProducer producer = new StandardOutputProducer(mockPrintStream);

        producer.send("foo");

        Mockito.verify(mockPrintStream).println("foo");
    }

}