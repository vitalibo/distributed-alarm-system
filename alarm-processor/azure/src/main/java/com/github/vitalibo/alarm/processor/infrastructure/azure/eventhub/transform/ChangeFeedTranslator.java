package com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.transform;

import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb.ChangeFeed;
import com.microsoft.azure.eventhubs.EventData;

public class ChangeFeedTranslator {

    private ChangeFeedTranslator() {
    }

    public static ChangeFeed fromEventData(EventData eventData) {
        byte[] bytes = eventData.getBytes();
        return Jackson.fromJsonString(bytes, ChangeFeed.class);
    }

}