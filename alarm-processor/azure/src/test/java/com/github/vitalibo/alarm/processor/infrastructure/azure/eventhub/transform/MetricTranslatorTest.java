package com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.transform;

import com.github.vitalibo.alarm.processor.core.model.Metric;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import com.microsoft.azure.eventhubs.EventData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;

public class MetricTranslatorTest {

    @Test
    public void testFromEventData() {
        EventData eventData = EventData.create(Resources.asString("/Metric.json").getBytes());

        Metric actual = MetricTranslator.fromEventData(eventData);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "foo");
        Assert.assertEquals(actual.getTimestamp(), OffsetDateTime.parse("2020-01-01T00:00:00Z"));
        Assert.assertEquals(actual.getValue(), 1.23);
    }

}