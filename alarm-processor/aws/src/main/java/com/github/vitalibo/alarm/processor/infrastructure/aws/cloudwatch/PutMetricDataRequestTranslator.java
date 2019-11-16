package com.github.vitalibo.alarm.processor.infrastructure.aws.cloudwatch;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import lombok.RequiredArgsConstructor;
import org.apache.spark.streaming.scheduler.BatchInfo;
import org.apache.spark.streaming.scheduler.OutputOperationInfo;
import scala.collection.Iterator;

import java.util.Date;

@RequiredArgsConstructor
class PutMetricDataRequestTranslator {

    private final String streamName;

    PutMetricDataRequest from(BatchInfo batchInfo) {
        return new PutMetricDataRequest()
            .withNamespace("Spark Streaming")
            .withMetricData(
                withRecords(batchInfo),
                withSchedulingDelay(batchInfo),
                withProcessingDelay(batchInfo),
                withTotalDelay(batchInfo),
                withFailedTasks(batchInfo));
    }

    private MetricDatum withRecords(BatchInfo batchInfo) {
        return new MetricDatum()
            .withMetricName("Records")
            .withTimestamp(timestamp(batchInfo))
            .withValue(value(batchInfo.numRecords()))
            .withUnit(StandardUnit.Count)
            .withDimensions(withStreamName(streamName));

    }

    private MetricDatum withSchedulingDelay(BatchInfo batchInfo) {
        return new MetricDatum()
            .withMetricName("Scheduling Delay")
            .withTimestamp(timestamp(batchInfo))
            .withValue(value(batchInfo.schedulingDelay().get()))
            .withUnit(StandardUnit.Milliseconds)
            .withDimensions(withStreamName(streamName));
    }

    private MetricDatum withProcessingDelay(BatchInfo batchInfo) {
        return new MetricDatum()
            .withMetricName("Processing Delay")
            .withTimestamp(timestamp(batchInfo))
            .withValue(value(batchInfo.processingDelay().get()))
            .withUnit(StandardUnit.Milliseconds)
            .withDimensions(withStreamName(streamName));
    }

    private MetricDatum withTotalDelay(BatchInfo batchInfo) {
        return new MetricDatum()
            .withMetricName("Total Delay")
            .withTimestamp(timestamp(batchInfo))
            .withValue(value(batchInfo.totalDelay().get()))
            .withUnit(StandardUnit.Milliseconds)
            .withDimensions(withStreamName(streamName));
    }

    private MetricDatum withFailedTasks(BatchInfo batchInfo) {
        return new MetricDatum()
            .withMetricName("Failed Tasks")
            .withTimestamp(timestamp(batchInfo))
            .withValue(value(numberFailedTasks(batchInfo)))
            .withUnit(StandardUnit.Count)
            .withDimensions(withStreamName(streamName));
    }

    private static Dimension withStreamName(String streamName) {
        return new Dimension()
            .withName("Stream Name")
            .withValue(streamName);
    }

    private static Date timestamp(BatchInfo batchInfo) {
        return new Date(batchInfo.batchTime().milliseconds());
    }

    private static long numberFailedTasks(BatchInfo batchInfo) {
        long failedTasks = 0;
        Iterator<OutputOperationInfo> iterator = batchInfo.outputOperationInfos().valuesIterator();
        while (iterator.hasNext()) {
            OutputOperationInfo info = iterator.next();
            if (info.failureReason().isDefined()) {
                failedTasks++;
            }
        }

        return failedTasks;
    }

    private static Double value(Object value) {
        return ((Long) value).doubleValue();
    }

}