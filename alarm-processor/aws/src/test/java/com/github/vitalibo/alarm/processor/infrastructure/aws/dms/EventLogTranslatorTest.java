package com.github.vitalibo.alarm.processor.infrastructure.aws.dms;

import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.model.Rule;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Iterator;

import static com.github.vitalibo.alarm.processor.core.model.EventLog.Type.*;

public class EventLogTranslatorTest {

    @Test
    public void testFromNonDataDMSEvent() {
        DMSEvent.Metadata metadata = new DMSEvent.Metadata();
        metadata.setRecordType("unknown");
        DMSEvent event = new DMSEvent();
        event.setMetadata(metadata);

        Iterator<EventLog<Rule>> actual = EventLogTranslator.fromDMSEvent(event);

        Assert.assertNotNull(actual);
        Assert.assertFalse(actual.hasNext());
    }

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {"load", Load}, {"insert", Insert}, {"update", Update}, {"delete", Delete}
        };
    }

    @Test(dataProvider = "samples")
    public void testFromDMSEvent(String operation, EventLog.Type type) {
        DMSEvent.Metadata metadata = new DMSEvent.Metadata();
        metadata.setRecordType("data");
        metadata.setSchemaName("foo");
        metadata.setTableName("bar");
        metadata.setTimestamp("2019-05-15T01:51:38.301411Z");
        metadata.setOperation(operation);
        DMSEvent event = new DMSEvent();
        event.setMetadata(metadata);
        event.setData(Collections.singletonMap("id", "4f39b43a"));

        Iterator<EventLog<Rule>> actual = EventLogTranslator.fromDMSEvent(event);

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.hasNext());
        EventLog<Rule> eventLog = actual.next();
        Assert.assertEquals(eventLog.getTable(), "foo.bar");
        Assert.assertEquals(eventLog.getTimestamp(), OffsetDateTime.of(2019, 5, 15, 1, 51, 38, 301411000, ZoneOffset.UTC));
        Assert.assertEquals(eventLog.getType(), type);
        Rule rule = eventLog.getPayload();
        Assert.assertEquals(rule.getId(), "4f39b43a");
    }

}