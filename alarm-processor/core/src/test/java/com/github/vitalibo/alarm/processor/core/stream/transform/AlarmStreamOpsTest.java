package com.github.vitalibo.alarm.processor.core.stream.transform;

import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.model.Metric;
import com.github.vitalibo.alarm.processor.core.model.Rule;
import org.apache.spark.streaming.State;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import scala.Tuple2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.vitalibo.alarm.processor.core.model.Rule.Condition.*;
import static com.github.vitalibo.alarm.processor.core.util.ScalaTypes.tuple;

public class AlarmStreamOpsTest {

    @Mock
    private State<Map<String, Rule>> mockRuleState;
    @Mock
    private State<Alarm.State> mockAlarmState;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMetricNameAsKey1() {
        Metric metric = new Metric();
        metric.setName("foo");
        metric.setValue(1.23);

        Tuple2<String, Metric> actual = AlarmStreamOps.metricNameAsKey(metric);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual._1, "foo");
        Assert.assertEquals(actual._2, metric);
    }

    @Test
    public void testMetricNameAsKey2() {
        Rule rule = new Rule();
        rule.setMetricName("foo");
        rule.setId("bar");
        EventLog<Rule> eventLog = new EventLog<>();
        eventLog.setType(EventLog.Type.Update);
        eventLog.setPayload(rule);

        Tuple2<String, EventLog<Rule>> actual = AlarmStreamOps.metricNameAsKey(eventLog);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual._1, "foo");
        Assert.assertEquals(actual._2, eventLog);
    }

    @Test
    public void testRuleIdAsKey() {
        Metric metric = new Metric();
        metric.setName("foo");
        Rule rule = new Rule();
        rule.setId("bar");

        Tuple2<String, Tuple2<Metric, Rule>> actual =
            AlarmStreamOps.ruleIdAsKey(tuple("baz", tuple(metric, rule)));

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual._1, "bar");
        Assert.assertEquals(actual._2, tuple(metric, rule));
    }


    @Test
    public void testUpdateStateInsert() {
        Rule rule = new Rule();
        rule.setMetricName("foo");
        rule.setId("bar");
        EventLog<Rule> eventLog = new EventLog<>();
        eventLog.setType(EventLog.Type.Load);
        eventLog.setPayload(rule);

        AlarmStreamOps.updateState(eventLog, mockRuleState);

        Mockito.verify(mockRuleState).exists();
        Mockito.verify(mockRuleState, Mockito.never()).get();
        Mockito.verify(mockRuleState).update(Collections.singletonMap("bar", rule));
    }

    @Test
    public void testUpdateStateUpdate() {
        Rule oldRule = new Rule();
        oldRule.setMetricName("old");
        Mockito.when(mockRuleState.exists()).thenReturn(true);
        Mockito.when(mockRuleState.get()).thenReturn(
            new HashMap<>(Collections.singletonMap("bar", oldRule)));
        Rule newRule = new Rule();
        newRule.setMetricName("foo");
        newRule.setId("bar");
        EventLog<Rule> eventLog = new EventLog<>();
        eventLog.setType(EventLog.Type.Insert);
        eventLog.setPayload(newRule);

        AlarmStreamOps.updateState(eventLog, mockRuleState);

        Mockito.verify(mockRuleState).exists();
        Mockito.verify(mockRuleState).get();
        Mockito.verify(mockRuleState).update(Collections.singletonMap("bar", newRule));
    }

    @Test
    public void testUpdateStateDelete() {
        Rule oldRule = new Rule();
        oldRule.setMetricName("foo");
        Mockito.when(mockRuleState.exists()).thenReturn(true);
        Mockito.when(mockRuleState.get()).thenReturn(
            new HashMap<>(Collections.singletonMap("bar", oldRule)));
        Rule newRule = new Rule();
        newRule.setMetricName("foo");
        newRule.setId("bar");
        EventLog<Rule> eventLog = new EventLog<>();
        eventLog.setType(EventLog.Type.Delete);
        eventLog.setPayload(newRule);

        AlarmStreamOps.updateState(eventLog, mockRuleState);

        Mockito.verify(mockRuleState).exists();
        Mockito.verify(mockRuleState).get();
        Mockito.verify(mockRuleState).update(Collections.emptyMap());
    }

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {1.9, GreaterThanOrEqualToThreshold, 2.0, Alarm.State.Ok},
            {2.0, GreaterThanOrEqualToThreshold, 2.0, Alarm.State.Alarm},
            {2.1, GreaterThanOrEqualToThreshold, 2.0, Alarm.State.Alarm},
            {1.9, GreaterThanThreshold, 2.0, Alarm.State.Ok},
            {2.0, GreaterThanThreshold, 2.0, Alarm.State.Ok},
            {2.1, GreaterThanThreshold, 2.0, Alarm.State.Alarm},
            {2.1, LessThanOrEqualToThreshold, 2.0, Alarm.State.Ok},
            {2.0, LessThanOrEqualToThreshold, 2.0, Alarm.State.Alarm},
            {1.9, LessThanOrEqualToThreshold, 2.0, Alarm.State.Alarm},
            {2.1, LessThanThreshold, 2.0, Alarm.State.Ok},
            {2.0, LessThanThreshold, 2.0, Alarm.State.Ok},
            {1.9, LessThanThreshold, 2.0, Alarm.State.Alarm}
        };
    }

    @Test(dataProvider = "samples")
    public void testTriggerAlarm(Double value, Rule.Condition condition, Double threshold, Alarm.State expected) {
        Metric metric = new Metric();
        metric.setName("foo");
        metric.setValue(value);
        Rule rule = new Rule();
        rule.setId("bar");
        rule.setCondition(condition);
        rule.setThreshold(threshold);

        List<Alarm> actual = AlarmStreamOps.triggerAlarm(metric, rule, mockAlarmState);

        Assert.assertNotNull(actual);
        Assert.assertFalse(actual.isEmpty());
        Alarm alarm = actual.get(0);
        Assert.assertEquals(alarm.getMetricName(), "foo");
        Assert.assertEquals(alarm.getRuleId(), "bar");
        Assert.assertEquals(alarm.getState(), expected);
    }

    @DataProvider
    public Object[][] samplesChangeState() {
        return new Object[][]{
            {Alarm.State.Alarm, Alarm.State.Ok, Collections.singletonList(Alarm.State.Pending)},
            {Alarm.State.Ok, Alarm.State.Alarm, Collections.singletonList(Alarm.State.Pending)},
            {Alarm.State.Pending, Alarm.State.Ok, Collections.singletonList(Alarm.State.Ok)},
            {Alarm.State.Pending, Alarm.State.Alarm, Collections.singletonList(Alarm.State.Alarm)},
            {Alarm.State.Alarm, Alarm.State.Alarm, Collections.emptyList()},
            {Alarm.State.Ok, Alarm.State.Ok, Collections.emptyList()},
        };
    }

    @Test(dataProvider = "samplesChangeState")
    public void testTriggerAlarmChangeState(Alarm.State previous, Alarm.State current, List<Alarm.State> expected) {
        Mockito.when(mockAlarmState.exists()).thenReturn(true);
        Mockito.when(mockAlarmState.get()).thenReturn(previous);
        Metric metric = new Metric();
        metric.setValue(1.0);
        Rule rule = new Rule();
        rule.setCondition(current == Alarm.State.Ok ? GreaterThanThreshold : LessThanThreshold);
        rule.setThreshold(2.0);

        List<Alarm> actual = AlarmStreamOps.triggerAlarm(metric, rule, mockAlarmState);

        Assert.assertNotNull(actual);
        Assert.assertEquals(
            actual.stream()
                .map(Alarm::getState)
                .collect(Collectors.toList()),
            expected);
    }

}