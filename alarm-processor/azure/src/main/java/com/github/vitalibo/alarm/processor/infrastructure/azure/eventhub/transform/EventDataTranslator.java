package com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.transform;

import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.microsoft.azure.eventhubs.EventData;

public class EventDataTranslator {

    private EventDataTranslator() {
    }

    public static EventData fromAlarm(Alarm alarm) {
        String json = Jackson.toJsonString(alarm);
        return EventData.create(json.getBytes());
    }

}