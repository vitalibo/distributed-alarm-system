package com.github.vitalibo.alarm.processor.core.model;

import com.github.vitalibo.alarm.processor.core.TestHelper;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MetricTest {

    @Test
    public void testSerDe() {
        Metric expected = Jackson.fromJsonString(
            Resources.asString("/Metric.json"), Metric.class);

        Metric actual = TestHelper.serDe(expected);

        Assert.assertEquals(actual, expected);
    }

}