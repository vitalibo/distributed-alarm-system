package com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.transform;

import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import com.microsoft.azure.eventhubs.EventData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EventDataTranslatorTest {

    @Test
    public void testFromAlarm() {
        Alarm alarm = new Alarm();
        alarm.setMetricName("foo");
        alarm.setRuleId("bar");
        alarm.setState(Alarm.State.Ok);
        String expected = Jackson.toJsonString(
            Jackson.fromJsonString(
                Resources.asString("/Alarm.json"),
                Object.class));

        EventData actual = EventDataTranslator.fromAlarm(alarm);

        Assert.assertNotNull(actual);
        Assert.assertEquals(new String(actual.getBytes()), expected);
    }

}