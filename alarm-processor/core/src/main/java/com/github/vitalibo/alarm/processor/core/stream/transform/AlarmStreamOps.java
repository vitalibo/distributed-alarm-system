package com.github.vitalibo.alarm.processor.core.stream.transform;

import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.model.Metric;
import com.github.vitalibo.alarm.processor.core.model.Rule;
import com.github.vitalibo.alarm.processor.core.store.AlarmStore;
import com.github.vitalibo.alarm.processor.core.store.RuleStore;
import lombok.RequiredArgsConstructor;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.streaming.State;
import scala.Option;
import scala.Tuple2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static Function3<String, Optional<Tuple2<Metric, Rule>>, State<Alarm.State>, List<Alarm>> triggerAlarm(AlarmStore store) {
        return (ruleId, value, state) -> {
            if (value.isPresent()) {
                Tuple2<Metric, Rule> tuple = value.get();
                return triggerAlarm(tuple._1, tuple._2, new AlarmState(state, store, ruleId));
            }

            return Collections.emptyList();
        };
    }

    static List<Alarm> triggerAlarm(Metric metric, Rule rule, AlarmState state) {
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

    public static List<Tuple2<String, Map<String, Rule>>> initialRuleState(RuleStore store) {
        return store.getAll()
            .stream()
            .map(rule -> tuple(rule.getMetricName(), rule))
            .collect(Collectors.groupingBy(Tuple2::_1,
                Collectors.mapping(o -> o._2, Collectors.toMap(tuple -> tuple.getId(), o -> o))))
            .entrySet().stream()
            .map(entry -> tuple(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public static List<Tuple2<String, Alarm.State>> initialAlarmState(AlarmStore store) {
        return store.getAll()
            .stream()
            .map(o -> tuple(o.getRuleId(), o.getState()))
            .collect(Collectors.toList());
    }

    @RequiredArgsConstructor
    static class AlarmState {

        private final State<Alarm.State> delegate;
        private final AlarmStore store;
        private final String ruleId;

        boolean exists() {
            return delegate.exists();
        }

        public Alarm.State get() {
            return delegate.get();
        }

        void update(Alarm.State state) {
            delegate.update(state);
            store.update(ruleId, state);
        }

        void remove() {
            delegate.remove();
            store.remove(ruleId);
        }

    }

}