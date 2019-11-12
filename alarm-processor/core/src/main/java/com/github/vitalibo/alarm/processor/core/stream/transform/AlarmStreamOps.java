package com.github.vitalibo.alarm.processor.core.stream.transform;

import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.model.Metric;
import com.github.vitalibo.alarm.processor.core.model.Rule;
import org.apache.spark.api.java.Optional;
import org.apache.spark.streaming.State;
import scala.Option;
import scala.Tuple2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.vitalibo.alarm.processor.core.model.Alarm.State.*;
import static com.github.vitalibo.alarm.processor.core.util.ScalaTypes.tuple;


public final class AlarmStreamOps {

    private AlarmStreamOps() {
    }

    public static Tuple2<String, Metric> metricNameAsKey(Metric metric) {
        return tuple(
            metric.getName(),
            metric);
    }

    public static Tuple2<String, EventLog<Rule>> metricNameAsKey(EventLog<Rule> eventLog) {
        return tuple(
            eventLog.getPayload()
                .getMetricName(),
            eventLog);
    }

    public static Tuple2<String, Tuple2<Metric, Rule>> ruleIdAsKey(Tuple2<String, Tuple2<Metric, Rule>> tuple) {
        return tuple(
            tuple._2
                ._2.getId(),
            tuple._2);
    }


    public static Option updateState(String key, Optional<EventLog<Rule>> value, State<Map<String, Rule>> state) {
        if (value.isPresent()) {
            updateState(value.get(), state);
        }

        return Option.empty();
    }

    static void updateState(EventLog<Rule> eventLog, State<Map<String, Rule>> state) {
        Map<String, Rule> rules = state.exists() ? state.get() : new HashMap<>();
        Rule rule = eventLog.getPayload();

        switch (eventLog.getType()) {
            case Load:
            case Insert:
            case Update:
                rules.put(rule.getId(), rule);
                break;
            case Delete:
                rules.remove(rule.getId());
                break;
            default:
                throw new IllegalStateException();
        }

        state.update(rules);
    }

    public static List<Alarm> triggerAlarm(String ruleId, Optional<Tuple2<Metric, Rule>> value, State<Alarm.State> state) {
        if (value.isPresent()) {
            Tuple2<Metric, Rule> tuple = value.get();
            return triggerAlarm(tuple._1, tuple._2, state);
        }

        return Collections.emptyList();
    }

    static List<Alarm> triggerAlarm(Metric metric, Rule rule, State<Alarm.State> state) {
        Alarm.State currentState = breakThreshold(metric, rule) ? Alarm : Ok;
        if (state.exists()) {
            Alarm.State previous = state.get();
            if (previous == currentState) {
                return Collections.emptyList();
            }

            if (previous != Pending) {
                currentState = Pending;
            }
        }

        state.update(currentState);
        return Collections.singletonList(new Alarm()
            .withMetricName(metric.getName())
            .withRuleId(rule.getId())
            .withState(currentState));
    }

    private static boolean breakThreshold(Metric metric, Rule rule) {
        switch (rule.getCondition()) {
            case GreaterThanOrEqualToThreshold:
                return metric.getValue() >= rule.getThreshold();
            case GreaterThanThreshold:
                return metric.getValue() > rule.getThreshold();
            case LessThanOrEqualToThreshold:
                return metric.getValue() <= rule.getThreshold();
            case LessThanThreshold:
                return metric.getValue() < rule.getThreshold();
            default:
                throw new IllegalStateException();
        }
    }

}