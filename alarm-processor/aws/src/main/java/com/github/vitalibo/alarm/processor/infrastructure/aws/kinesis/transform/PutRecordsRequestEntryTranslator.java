package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.transform;

import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.util.Jackson;

import java.nio.ByteBuffer;

public final class PutRecordsRequestEntryTranslator {

    private PutRecordsRequestEntryTranslator() {
    }

    public static PutRecordsRequestEntry fromAlarm(Alarm alarm) {
        return new PutRecordsRequestEntry()
            .withData(ByteBuffer.wrap((Jackson.toJsonString(alarm) + '\n').getBytes()))
            .withPartitionKey(alarm.getRuleId());
    }

}