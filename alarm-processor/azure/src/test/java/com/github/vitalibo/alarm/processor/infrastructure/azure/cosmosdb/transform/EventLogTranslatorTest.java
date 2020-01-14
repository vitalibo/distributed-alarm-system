package com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb.transform;

import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.model.Rule;
import com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb.ChangeFeed;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.*;

public class EventLogTranslatorTest {

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {document(null, 1577836800, 1.24), EventLog.Type.Insert, 1.24},
            {document(-1, 1577836800L, 1.24f), EventLog.Type.Update, 1.24},
            {document(1, 1577836800, 1), EventLog.Type.Delete, 1.0},
            {document(1L, 1577836800L, 1.0), EventLog.Type.Delete, 1.0},
        };
    }

    @Test(dataProvider = "samples")
    public void testFromChangeFeed(Map<String, ?> document, EventLog.Type expectedType, Double expectedThreshold) {
        ChangeFeed event = new ChangeFeed();
        event.setDocuments(Collections.singletonList(document));
        event.setFunctionName("function");

        Iterator<EventLog<Rule>> iterator = EventLogTranslator.fromChangeFeed(event);

        Assert.assertNotNull(iterator);
        ArrayList<EventLog<Rule>> actual = new ArrayList<>();
        iterator.forEachRemaining(actual::add);
        Assert.assertEquals(actual.size(), 1);
        EventLog<Rule> eventLog = actual.get(0);
        Assert.assertEquals(eventLog.getTable(), "function");
        Assert.assertEquals(eventLog.getType(), expectedType);
        Assert.assertEquals(eventLog.getTimestamp(), OffsetDateTime.parse("2020-01-01T00:00:00Z"));
        Rule rule = eventLog.getPayload();
        Assert.assertEquals(rule.getId(), "foo");
        Assert.assertEquals(rule.getMetricName(), "bar");
        Assert.assertEquals(rule.getCondition(), Rule.Condition.GreaterThanThreshold);
        Assert.assertEquals(rule.getThreshold(), expectedThreshold);
    }

    private static HashMap<String, Object> document(Object ttl, Object timestamp, Object threshold) {
        return new HashMap<String, Object>() {{
            put("id", "foo");
            put("metricName", "bar");
            put("condition", "GreaterThanThreshold");
            put("threshold", threshold);

            put("_ts", timestamp);
            if (Objects.nonNull(ttl)) {
                put("ttl", ttl);
            }
        }};
    }

}