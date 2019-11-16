package com.github.vitalibo.alarm.processor.infrastructure.aws.cloudwatch;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import org.apache.spark.streaming.scheduler.BatchInfo;
import org.apache.spark.streaming.scheduler.StreamingListenerBatchCompleted;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CloudWatchMetricsListenerTest {

    @Mock
    private PutMetricDataRequestTranslator mockPutMetricDataRequestTranslator;
    @Mock
    private AmazonCloudWatchAsync mockAmazonCloudWatchAsync;
    @Mock
    private StreamingListenerBatchCompleted mockStreamingListenerBatchCompleted;
    @Mock
    private BatchInfo mockBatchInfo;
    @Mock
    private PutMetricDataRequest mockPutMetricDataRequest;

    private CloudWatchMetricsListener metricsListener;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        metricsListener = new CloudWatchMetricsListener(mockPutMetricDataRequestTranslator, mockAmazonCloudWatchAsync);
    }

    @Test
    public void testOnBatchCompleted() {
        Mockito.when(mockStreamingListenerBatchCompleted.batchInfo())
            .thenReturn(mockBatchInfo);
        Mockito.when(mockBatchInfo.numRecords()).thenReturn(5L);
        Mockito.when(mockPutMetricDataRequestTranslator.from(Mockito.any()))
            .thenReturn(mockPutMetricDataRequest);

        metricsListener.onBatchCompleted(mockStreamingListenerBatchCompleted);

        Mockito.verify(mockPutMetricDataRequestTranslator).from(mockBatchInfo);
        Mockito.verify(mockAmazonCloudWatchAsync).putMetricDataAsync(mockPutMetricDataRequest);
    }

    @Test
    public void testOnEmptyBatchCompleted() {
        Mockito.when(mockStreamingListenerBatchCompleted.batchInfo())
            .thenReturn(mockBatchInfo);
        Mockito.when(mockBatchInfo.numRecords()).thenReturn(0L);
        Mockito.when(mockPutMetricDataRequestTranslator.from(Mockito.any()))
            .thenReturn(mockPutMetricDataRequest);

        metricsListener.onBatchCompleted(mockStreamingListenerBatchCompleted);

        Mockito.verify(mockPutMetricDataRequestTranslator, Mockito.never()).from(mockBatchInfo);
        Mockito.verify(mockAmazonCloudWatchAsync, Mockito.never()).putMetricDataAsync(mockPutMetricDataRequest);
    }

}