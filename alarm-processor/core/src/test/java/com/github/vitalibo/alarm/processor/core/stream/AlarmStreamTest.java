package com.github.vitalibo.alarm.processor.core.stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.vitalibo.alarm.processor.core.Sink;
import com.github.vitalibo.alarm.processor.core.Source;
import com.github.vitalibo.alarm.processor.core.Spark;
import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.model.Metric;
import com.github.vitalibo.alarm.processor.core.model.Rule;
import com.github.vitalibo.alarm.processor.core.store.AlarmStore;
import com.github.vitalibo.alarm.processor.core.store.RuleStore;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import com.holdenkarau.spark.testing.JavaStreamingSuiteBase;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.mockito.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class AlarmStreamTest extends JavaStreamingSuiteBase {

    @Mock
    private Spark mockSpark;
    @Mock
    private Source<EventLog<Rule>> mockRuleSource;
    @Mock
    private Source<Metric> mockMetricSource;
    @Mock
    private Sink<Alarm> mockSink;
    @Mock(serializable = true)
    private AlarmStore mockAlarmStore;
    @Mock(serializable = true)
    private RuleStore mockRuleStore;
    @Captor
    private ArgumentCaptor<JavaDStream<Alarm>> captorJavaDStream;

    private JavaSparkContext sparkContext;
    private AlarmStream stream;

    @BeforeClass
    public void setupSparkContext() {
        super.runBefore();
        sparkContext = new JavaSparkContext(sc());
    }

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        stream = new AlarmStream(
            mockMetricSource, mockRuleSource, mockSink,
            mockAlarmStore, mockRuleStore, "file://tmp/");
    }

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {"sample#1"},
            {"sample#2"},
            {"sample#3"}
        };
    }

    @Test(dataProvider = "samples")
    public void testProcess(String resource) {
        List<List<EventLog<Rule>>> rules = asRules(resource);
        List<List<Metric>> metrics = asMetrics(resource);
        List<List<Alarm>> alarms = asAlarms(resource);

        testOperation(rules, metrics, this::process, alarms);
    }

    private JavaDStream<Alarm> process(JavaDStream<EventLog<Rule>> rules, JavaDStream<Metric> metrics) {
        Mockito.when(mockSpark.createStream(mockRuleSource)).thenReturn(rules);
        Mockito.when(mockSpark.createStream(mockMetricSource)).thenReturn(metrics);
        Mockito.doAnswer(o -> sparkContext.parallelizePairs(o.getArgument(0)))
            .when(mockSpark).parallelizePairs(Mockito.any());

        stream.process(mockSpark);

        Mockito.verify(mockSpark).writeStream(Mockito.eq(mockSink), captorJavaDStream.capture());
        return captorJavaDStream.getValue();
    }

    private static List<List<EventLog<Rule>>> asRules(String resource) {
        return asObject("/AlarmStream/" + resource + "/Rules.json", new TypeReference<List<List<EventLog<Rule>>>>() {});
    }

    private static List<List<Metric>> asMetrics(String resource) {
        return asObject("/AlarmStream/" + resource + "/Metrics.json", new TypeReference<List<List<Metric>>>() {});
    }

    private static List<List<Alarm>> asAlarms(String resource) {
        return asObject("/AlarmStream/" + resource + "/Alarms.json", new TypeReference<List<List<Alarm>>>() {});
    }

    private static <T> T asObject(String resource, TypeReference<T> typeReference) {
        return Jackson.fromJsonString(Resources.asInputStream(resource), typeReference);
    }

}