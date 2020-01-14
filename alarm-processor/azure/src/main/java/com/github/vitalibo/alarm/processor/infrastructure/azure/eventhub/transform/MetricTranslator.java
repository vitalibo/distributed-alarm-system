package com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.transform;

import com.github.vitalibo.alarm.processor.core.model.Metric;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.microsoft.azure.eventhubs.EventData;

public class MetricTranslator {

    private MetricTranslator() {
    }

    public static Metric fromEventData(EventData eventData) {
        byte[] bytes = eventData.getBytes();
        return Jackson.fromJsonString(bytes, Metric.class);
    }

}