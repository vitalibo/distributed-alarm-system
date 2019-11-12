package com.github.vitalibo.alarm.processor.core.model;

import com.github.vitalibo.alarm.processor.core.TestHelper;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AlarmTest {

    @Test
    public void testSerDe() {
        Alarm expected = Jackson.fromJsonString(
            Resources.asString("/Alarm.json"), Alarm.class);

        Alarm actual = TestHelper.serDe(expected);

        Assert.assertEquals(actual, expected);
    }

}