package com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub.transform;

import com.github.vitalibo.alarm.processor.core.util.Resources;
import com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb.ChangeFeed;
import com.microsoft.azure.eventhubs.EventData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class ChangeFeedTranslatorTest {

    @Test
    public void testFromEventData() {
        EventData eventData = EventData.create(Resources.asString("/ChangeFeed.json").getBytes());

        ChangeFeed actual = ChangeFeedTranslator.fromEventData(eventData);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getFunctionName(), "CosmosDBTrigger");
        Assert.assertEquals(actual.getInvocationId(), "56cfc1ec-a318-4664-8ef7-6608ce8a36de");
        List<Map<String, ?>> documents = actual.getDocuments();
        Assert.assertNotNull(documents);
        Assert.assertEquals(documents.size(), 1);
        Assert.assertEquals(documents.get(0).get("id"), "1S3F");
    }

}