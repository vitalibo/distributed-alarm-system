package com.github.vitalibo.alarm.processor.core.stream;

import com.github.vitalibo.alarm.processor.core.Sink;
import com.github.vitalibo.alarm.processor.core.Source;
import com.github.vitalibo.alarm.processor.core.Spark;
import com.github.vitalibo.alarm.processor.core.Stream;
import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.model.Metric;
import com.github.vitalibo.alarm.processor.core.model.Rule;
import com.github.vitalibo.alarm.processor.core.store.AlarmStore;
import com.github.vitalibo.alarm.processor.core.store.RuleStore;
import com.github.vitalibo.alarm.processor.core.stream.transform.AlarmStreamOps;
import lombok.RequiredArgsConstructor;
import org.apache.spark.streaming.StateSpec;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class AlarmStream implements Stream, Serializable {

    private final transient Source<Metric> metricSource;
    private final transient Source<EventLog<Rule>> ruleSource;
    private final transient Sink<Alarm> alarmSink;

    private final AlarmStore alarmStore;
    private final RuleStore ruleStore;

    private final String checkpoint;

    @Override
    public void process(Spark spark) {
        spark.checkpoint(checkpoint);

        JavaPairDStream<String, Rule> rules = spark.createStream(ruleSource)
            .mapToPair(AlarmStreamOps::metricNameAsKey)
            .mapWithState(
                StateSpec.function(AlarmStreamOps::updateState)
                    .initialState(spark.parallelizePairs(AlarmStreamOps.initialRuleState(ruleStore))))
            .stateSnapshots()
            .flatMapValues(Map::values);

        JavaDStream<Alarm> alarms = spark.createStream(metricSource)
            .mapToPair(AlarmStreamOps::metricNameAsKey)
            .join(rules)
            .mapToPair(AlarmStreamOps::ruleIdAsKey)
            .mapWithState(
                StateSpec.function(AlarmStreamOps.triggerAlarm(alarmStore))
                    .initialState(spark.parallelizePairs(AlarmStreamOps.initialAlarmState(alarmStore))))
            .flatMap(Collection::iterator);

        spark.writeStream(alarmSink, alarms);
    }

}