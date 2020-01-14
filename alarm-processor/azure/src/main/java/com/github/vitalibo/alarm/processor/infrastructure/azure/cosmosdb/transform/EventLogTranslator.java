package com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb.transform;

import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.model.Rule;
import com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb.ChangeFeed;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class EventLogTranslator {

    private EventLogTranslator() {
    }

    public static Iterator<EventLog<Rule>> fromChangeFeed(ChangeFeed event) {
        return event.getDocuments()
            .stream()
            .map(document -> {
                EventLog<Rule> rule = EventLogTranslator.from(document);
                rule.setTable(event.getFunctionName());
                return rule;
            })
            .iterator();
    }

    private static EventLog<Rule> from(Map<String, ?> document) {
        EventLog<Rule> eventLog = new EventLog<>();

        eventLog.setType(
            parseType(parseLong(document.get("ttl"))));
        eventLog.setTimestamp(
            Instant.ofEpochSecond(parseLong(document.get("_ts")))
                .atOffset(ZoneOffset.UTC));

        Rule rule = new Rule();
        rule.setId((String) document.get("id"));
        rule.setMetricName((String) document.get("metricName"));
        rule.setCondition(Rule.Condition.valueOf((String) document.get("condition")));
        rule.setThreshold(parseDouble(document.get("threshold")));
        eventLog.setPayload(rule);

        return eventLog;
    }

    private static EventLog.Type parseType(Long ttl) {
        if (Objects.isNull(ttl)) {
            return EventLog.Type.Insert;
        }

        return ttl > 0 ? EventLog.Type.Delete : EventLog.Type.Update;
    }

    private static Long parseLong(Object o) {
        return Objects.isNull(o) ? null : Long.parseLong("" + o);
    }

    private static Double parseDouble(Object o) {
        return Objects.isNull(o) ? null : Double.parseDouble("" + o);
    }

}