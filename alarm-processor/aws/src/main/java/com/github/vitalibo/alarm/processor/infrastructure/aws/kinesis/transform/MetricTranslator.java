package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.transform;

import com.amazonaws.services.kinesis.model.Record;
import com.github.vitalibo.alarm.processor.core.model.Metric;
import com.github.vitalibo.alarm.processor.core.util.Jackson;

import java.nio.ByteBuffer;

public final class MetricTranslator {

    private MetricTranslator() {
    }

    public static Metric fromKinesisRecord(Record record) {
        ByteBuffer data = record.getData();
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        return Jackson.fromJsonString(bytes, Metric.class);
    }

}