package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis;

import com.github.vitalibo.alarm.processor.core.util.function.Singleton;
import com.holdenkarau.spark.testing.JavaStreamingSuiteBase;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KinesisStreamSinkTest extends JavaStreamingSuiteBase {

    @Mock
    private KinesisRecordsPublisher<String> mockKinesisRecordsPublisher;
    @Mock
    private Singleton<KinesisRecordsPublisher<String>> mockSingleton;

    private KinesisStreamSink<String> sink;

    @BeforeClass
    public void setUpSparkContext() {
        super.runBefore();
    }

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testWriteJavaDStream() {
        sink = new MockKinesisStreamSink();
        List<List<String>> input = Arrays.asList(
            Arrays.asList("foo", "bar"), Collections.singletonList("baz"));

        testOperation(input, this::write, input);
        Assert.assertEquals(MockKinesisStreamSink.result, new HashSet<>(Arrays.asList("foo", "bar", "baz")));
    }

    private JavaDStream<String> write(JavaDStream<String> input) {
        sink.write(new JavaStreamingContext(new JavaSparkContext(sc()), batchDuration()), input);
        return input;
    }

    @Test
    public void testWrite() {
        Mockito.when(mockSingleton.createOrGet()).thenReturn(mockKinesisRecordsPublisher);
        sink = new KinesisStreamSink<>(mockSingleton);
        List<String> list = Arrays.asList("foo", "bar");

        sink.write(list.iterator());

        Mockito.verify(mockSingleton).createOrGet();
        Mockito.verify(mockKinesisRecordsPublisher).publish("foo");
        Mockito.verify(mockKinesisRecordsPublisher).publish("bar");
        Mockito.verify(mockKinesisRecordsPublisher).close();
    }

    private static class MockKinesisStreamSink extends KinesisStreamSink<String> {
        private static volatile Set<String> result = Collections.newSetFromMap(new ConcurrentHashMap<>());

        MockKinesisStreamSink() {
            super(null);
            result.clear();
        }

        @Override
        void write(Iterator<String> iterator) {
            iterator.forEachRemaining(result::add);
        }
    }
}