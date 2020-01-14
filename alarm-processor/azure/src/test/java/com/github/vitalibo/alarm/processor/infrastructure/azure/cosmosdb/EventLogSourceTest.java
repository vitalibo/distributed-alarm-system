package com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.vitalibo.alarm.processor.core.Source;
import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import com.github.vitalibo.alarm.processor.core.util.function.Function;
import com.holdenkarau.spark.testing.JavaStreamingSuiteBase;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EventLogSourceTest extends JavaStreamingSuiteBase {

    @Mock
    private Source<ChangeFeed> mockSource;

    @BeforeClass
    public void setupSparkContext() {
        super.runBefore();
    }

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRead() {
        JavaStreamingContext context = new JavaStreamingContext(
            new JavaSparkContext(sc()), batchDuration());
        EventLogSource<Map<String, ?>> source =
            new EventLogSource<>(mockSource, EventLogSourceTest::transform);
        Function<JavaDStream<ChangeFeed>, JavaDStream<EventLog<Map<String, ?>>>> read =
            (original) -> {
                Mockito.when(mockSource.read(Mockito.any())).thenReturn(original);
                return source.read(context);
            };
        List<List<ChangeFeed>> input = asObject(
            "/EventLogSource/Input.json", new TypeReference<List<List<ChangeFeed>>>() {});
        List<List<EventLog<Map<String, ?>>>> output = asObject(
            "/EventLogSource/Output.json", new TypeReference<List<List<EventLog<Map<String, ?>>>>>() {});

        testOperation(input, read::apply, output);
    }

    private static Iterator<EventLog<Map<String, ?>>> transform(ChangeFeed event) {
        return event.getDocuments()
            .stream()
            .map(o -> {
                EventLog<Map<String, ?>> eventLog = new EventLog<>();
                eventLog.setPayload(o);
                eventLog.setTable(event.getFunctionName());
                return eventLog;
            }).iterator();
    }

    private static <T> T asObject(String resource, TypeReference<T> typeReference) {
        return Jackson.fromJsonString(Resources.asInputStream(resource), typeReference);
    }

}