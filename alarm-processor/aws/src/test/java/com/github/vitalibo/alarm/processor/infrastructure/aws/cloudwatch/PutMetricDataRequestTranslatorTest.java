package com.github.vitalibo.alarm.processor.infrastructure.aws.cloudwatch;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.scheduler.BatchInfo;
import org.apache.spark.streaming.scheduler.OutputOperationInfo;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import scala.Option;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.collection.immutable.Map$;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.vitalibo.alarm.processor.core.util.ScalaTypes.tuple;

public class PutMetricDataRequestTranslatorTest {

    @Mock
    private BatchInfo mockBatchInfo;
    @Mock
    private OutputOperationInfo mockOutputOperationInfo;

    private PutMetricDataRequestTranslator translator;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        translator = new PutMetricDataRequestTranslator("kinesisStreamName");
    }

    @Test
    public void testTranslate() {
        Mockito.when(mockBatchInfo.batchTime()).thenReturn(new Time(1514764800000L));
        Mockito.when(mockBatchInfo.numRecords()).thenReturn(100L);
        Mockito.when(mockBatchInfo.schedulingDelay()).thenReturn(option(123L));
        Mockito.when(mockBatchInfo.processingDelay()).thenReturn(option(124L));
        Mockito.when(mockBatchInfo.totalDelay()).thenReturn(option(125L));
        Mockito.when(mockBatchInfo.outputOperationInfos())
            .thenReturn(immutableMap(Collections.singletonMap(null, mockOutputOperationInfo)));
        Mockito.when(mockOutputOperationInfo.failureReason()).thenReturn(option("failure"));

        PutMetricDataRequest actual = translator.from(mockBatchInfo);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getNamespace(), "Spark Streaming");
        Map<String, MetricDatum> metricDatum = actual.getMetricData().stream()
            .collect(Collectors.toMap(MetricDatum::getMetricName, o -> o));
        assertRecords(metricDatum.get("Records"));
        assertSchedulingDelay(metricDatum.get("Scheduling Delay"));
        assertProcessingDelay(metricDatum.get("Processing Delay"));
        assertTotalDelay(metricDatum.get("Total Delay"));
        assertFailedTasks(metricDatum.get("Failed Tasks"));
    }

    private static void assertRecords(MetricDatum actual) {
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getTimestamp(), new Date(1514764800000L));
        Assert.assertEquals(actual.getValue(), 100.0);
        Assert.assertEquals(actual.getUnit(), String.valueOf(StandardUnit.Count));
        Assert.assertEquals(actual.getDimensions().size(), 1);
        assertDimension(actual.getDimensions().get(0));
    }

    private static void assertSchedulingDelay(MetricDatum actual) {
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getTimestamp(), new Date(1514764800000L));
        Assert.assertEquals(actual.getValue(), 123.0);
        Assert.assertEquals(actual.getUnit(), String.valueOf(StandardUnit.Milliseconds));
        Assert.assertEquals(actual.getDimensions().size(), 1);
        assertDimension(actual.getDimensions().get(0));
    }

    private static void assertProcessingDelay(MetricDatum actual) {
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getTimestamp(), new Date(1514764800000L));
        Assert.assertEquals(actual.getValue(), 124.0);
        Assert.assertEquals(actual.getUnit(), String.valueOf(StandardUnit.Milliseconds));
        Assert.assertEquals(actual.getDimensions().size(), 1);
        assertDimension(actual.getDimensions().get(0));
    }

    private static void assertTotalDelay(MetricDatum actual) {
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getTimestamp(), new Date(1514764800000L));
        Assert.assertEquals(actual.getValue(), 125.0);
        Assert.assertEquals(actual.getUnit(), String.valueOf(StandardUnit.Milliseconds));
        Assert.assertEquals(actual.getDimensions().size(), 1);
        assertDimension(actual.getDimensions().get(0));
    }

    private static void assertFailedTasks(MetricDatum actual) {
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getTimestamp(), new Date(1514764800000L));
        Assert.assertEquals(actual.getValue(), 1.0);
        Assert.assertEquals(actual.getUnit(), String.valueOf(StandardUnit.Count));
        Assert.assertEquals(actual.getDimensions().size(), 1);
        assertDimension(actual.getDimensions().get(0));
    }

    private static void assertDimension(Dimension actual) {
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "Stream Name");
        Assert.assertEquals(actual.getValue(), "kinesisStreamName");
    }

    private static <T> Option<T> option(T value) {
        return Option.apply(value);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> scala.collection.immutable.Map<K, V> immutableMap(Map<K, V> jmap) {
        List<Tuple2<K, V>> tuples = jmap.entrySet().stream()
            .map(o -> tuple(o.getKey(), o.getValue()))
            .collect(Collectors.toList());

        Seq<Tuple2<K, V>> scalaSeq = JavaConverters.asScalaBufferConverter(tuples).asScala().toSeq();
        return (scala.collection.immutable.Map<K, V>) Map$.MODULE$.apply(scalaSeq);
    }

}