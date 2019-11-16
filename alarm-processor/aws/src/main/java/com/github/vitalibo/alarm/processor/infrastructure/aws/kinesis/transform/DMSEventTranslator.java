package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.transform;

import com.amazonaws.services.kinesis.model.Record;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.github.vitalibo.alarm.processor.infrastructure.aws.dms.DMSEvent;

import java.nio.ByteBuffer;

public final class DMSEventTranslator {

    private DMSEventTranslator() {
    }

    public static DMSEvent fromKinesisRecord(Record record) {
        ByteBuffer data = record.getData();
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        return Jackson.fromJsonString(bytes, DMSEvent.class);
    }

}