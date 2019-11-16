package com.github.vitalibo.alarm.processor.core.util.function;

import com.github.vitalibo.alarm.processor.core.TestHelper;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PredicateTest {

    @Spy
    private Predicate<String> spyPredicate;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPredicateSerDe() {
        Predicate<String> predicate = "sAmplE"::equalsIgnoreCase;

        Predicate<String> actual = TestHelper.serDe(predicate);

        Assert.assertTrue(actual.test("sample"));
    }

    @Test
    public void testCall() {
        Mockito.doReturn(true).when(spyPredicate).test(Mockito.anyString());

        boolean actual = spyPredicate.call("foo");

        Assert.assertTrue(actual);
        Mockito.verify(spyPredicate).test("foo");
    }

}