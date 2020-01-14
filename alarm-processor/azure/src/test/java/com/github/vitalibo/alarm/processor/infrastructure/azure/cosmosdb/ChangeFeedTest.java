package com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb;

import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

public class ChangeFeedTest {

    private ChangeFeed changeFeed;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        changeFeed = new ChangeFeed();
    }

    @Test
    public void testWithDocuments() {
        changeFeed.withDocuments(
            Collections.singletonList(
                Collections.singletonMap("foo", "bar")));

        Assert.assertEquals(changeFeed.getDocuments(),
            Collections.singletonList(Collections.singletonMap("foo", "bar")));
    }

    @Test
    public void testWithFunctionName() {
        changeFeed.withFunctionName("foo");

        Assert.assertEquals(changeFeed.getFunctionName(), "foo");
    }

    @Test
    public void testWithInvocationId() {
        changeFeed.withInvocationId("bar");

        Assert.assertEquals(changeFeed.getInvocationId(), "bar");
    }

}