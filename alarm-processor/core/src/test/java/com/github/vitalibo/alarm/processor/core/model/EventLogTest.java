package com.github.vitalibo.alarm.processor.core.model;

import com.github.vitalibo.alarm.processor.core.TestHelper;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EventLogTest {

    @Test
    public void testSerDe() {
        EventLog expected = Jackson.fromJsonString(
            Resources.asString("/EventLog.json"), EventLog.class);

        EventLog actual = TestHelper.serDe(expected);

        Assert.assertEquals(actual, expected);
    }

}