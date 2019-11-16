package com.github.vitalibo.alarm.processor.infrastructure.aws.cloudwatch;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import lombok.RequiredArgsConstructor;
import org.apache.spark.streaming.scheduler.*;

@RequiredArgsConstructor
public class CloudWatchMetricsListener implements StreamingListener {

    private final PutMetricDataRequestTranslator translator;
    private final AmazonCloudWatchAsync cloudWatchAsync;

    public CloudWatchMetricsListener(String streamName, AmazonCloudWatchAsync cloudWatchAsync) {
        this(new PutMetricDataRequestTranslator(streamName), cloudWatchAsync);
    }

    @Override
    public void onStreamingStarted(StreamingListenerStreamingStarted streamingStarted) {
    }

    @Override
    public void onReceiverStarted(StreamingListenerReceiverStarted receiverStarted) {
    }

    @Override
    public void onReceiverError(StreamingListenerReceiverError receiverError) {
    }

    @Override
    public void onReceiverStopped(StreamingListenerReceiverStopped receiverStopped) {
    }

    @Override
    public void onBatchSubmitted(StreamingListenerBatchSubmitted batchSubmitted) {
    }

    @Override
    public void onBatchStarted(StreamingListenerBatchStarted batchStarted) {
    }

    @Override
    public void onBatchCompleted(StreamingListenerBatchCompleted batchCompleted) {
        BatchInfo batchInfo = batchCompleted.batchInfo();
        if (batchInfo.numRecords() <= 0) {
            return;
        }

        PutMetricDataRequest request = translator.from(batchInfo);
        cloudWatchAsync.putMetricDataAsync(request);
    }

    @Override
    public void onOutputOperationStarted(StreamingListenerOutputOperationStarted outputOperationStarted) {
    }

    @Override
    public void onOutputOperationCompleted(StreamingListenerOutputOperationCompleted outputOperationCompleted) {
    }

}