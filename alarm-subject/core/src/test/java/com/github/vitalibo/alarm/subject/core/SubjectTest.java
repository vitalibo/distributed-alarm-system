package com.github.vitalibo.alarm.subject.core;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SubjectTest {

    @Mock
    private GenericValue mockGenericValue;
    @Mock
    private Producer mockTarget;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRun() {
        Mockito.when(mockGenericValue.get()).thenReturn("foo");
        Subject generator = new Subject(
            mockGenericValue, mockTarget, 20, 2);

        generator.run();

        Mockito.verify(mockGenericValue, Mockito.times(2)).get();
        Mockito.verify(mockTarget, Mockito.times(2)).send("foo");
    }

}