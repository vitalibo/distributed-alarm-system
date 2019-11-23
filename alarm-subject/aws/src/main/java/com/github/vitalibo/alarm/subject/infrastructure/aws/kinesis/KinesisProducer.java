package com.github.vitalibo.alarm.subject.infrastructure.aws.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.github.vitalibo.alarm.subject.core.Producer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
@RequiredArgsConstructor
public class KinesisProducer implements Producer {

    private final String streamName;
    private final AmazonKinesis kinesis;

    @Override
    public synchronized void send(String value) {
        PutRecordRequest request = makePutRecordRequest(value);
        send(request, 100);
    }

    @SneakyThrows
    synchronized void send(PutRecordRequest request, long backOff) {
        try {
            kinesis.putRecord(request);
        } catch (ProvisionedThroughputExceededException e) {
            logger.warn("ProvisionedThroughputExceededException: Wait {} ms. MessageHash {}", backOff, request.hashCode());
            Thread.sleep(backOff);
            send(request, 2 * backOff);
        }
    }

    private PutRecordRequest makePutRecordRequest(String value) {
        return new PutRecordRequest()
            .withStreamName(streamName)
            .withData(ByteBuffer.wrap(value.getBytes()))
            .withPartitionKey(String.valueOf(value.hashCode()));
    }

}