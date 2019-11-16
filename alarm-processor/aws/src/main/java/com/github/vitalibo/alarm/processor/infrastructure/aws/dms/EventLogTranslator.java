package com.github.vitalibo.alarm.processor.infrastructure.aws.dms;

import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.model.Rule;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Iterator;

@Slf4j
public final class EventLogTranslator {

    private EventLogTranslator() {
    }

    public static Iterator<EventLog<Rule>> fromDMSEvent(DMSEvent event) {
        DMSEvent.Metadata metadata = event.getMetadata();
        if (!"data".equalsIgnoreCase(metadata.getRecordType())) {
            logger.warn("Non data event. Skip processing message: {}.", event);
            return Collections.emptyIterator();
        }

        EventLog<Rule> eventLog = new EventLog<>();
        eventLog.setTable(metadata.getSchemaName() + "." + metadata.getTableName());
        eventLog.setTimestamp(OffsetDateTime.parse(metadata.getTimestamp()));
        eventLog.setType(parseType(metadata.getOperation()));
        eventLog.setPayload(Jackson.transfrom(event.getData(), Rule.class));

        return Collections.singletonList(eventLog)
            .iterator();
    }

    private static EventLog.Type parseType(String type) {
        switch (type.toLowerCase()) {
            case "load":
                return EventLog.Type.Load;
            case "insert":
                return EventLog.Type.Insert;
            case "update":
                return EventLog.Type.Update;
            case "delete":
                return EventLog.Type.Delete;
            default:
                throw new IllegalArgumentException("Unsupported event log type.");
        }
    }

}