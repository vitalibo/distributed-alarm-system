package com.github.vitalibo.alarm.processor.infrastructure.aws.dms;

import com.github.vitalibo.alarm.processor.TestHelper;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DMSEventTest {

    @Test
    public void testSerDe() {
        DMSEvent expected = Jackson.fromJsonString(
            Resources.asString("/DMSEvent.json"), DMSEvent.class);

        DMSEvent actual = TestHelper.serDe(expected);

        Assert.assertEquals(actual, expected);
    }

}