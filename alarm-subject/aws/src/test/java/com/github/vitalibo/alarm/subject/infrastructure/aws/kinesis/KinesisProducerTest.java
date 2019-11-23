package com.github.vitalibo.alarm.subject.infrastructure.aws.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;

public class KinesisProducerTest {

    @Mock
    private AmazonKinesis mockAmazonKinesis;
    @Mock
    private PutRecordRequest mockPutRecordRequest;
    @Mock
    private PutRecordResult mockPutRecordResult;
    @Captor
    private ArgumentCaptor<PutRecordRequest> captorPutRecordRequest;

    private KinesisProducer spyKinesisProducer;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spyKinesisProducer = Mockito.spy(
            new KinesisProducer("foo", mockAmazonKinesis));
    }

    @Test
    public void testSend() {
        spyKinesisProducer.send("bar");

        Mockito.verify(spyKinesisProducer).send(captorPutRecordRequest.capture(), Mockito.eq(100L));
        PutRecordRequest request = captorPutRecordRequest.getValue();
        Assert.assertEquals(request.getStreamName(), "foo");
        Assert.assertEquals(request.getData(), ByteBuffer.wrap("bar".getBytes()));
    }

    @Test
    public void testSuccessSendRecord() {
        spyKinesisProducer.send(mockPutRecordRequest, 0);

        Mockito.verify(mockAmazonKinesis).putRecord(mockPutRecordRequest);
    }

    @Test
    public void testBackOffRetrySendRecord() {
        Mockito.when(mockAmazonKinesis.putRecord(mockPutRecordRequest))
            .thenThrow(ProvisionedThroughputExceededException.class)
            .thenThrow(ProvisionedThroughputExceededException.class)
            .thenReturn(mockPutRecordResult);

        spyKinesisProducer.send(mockPutRecordRequest, 1);

        Mockito.verify(mockAmazonKinesis, Mockito.times(3)).putRecord(mockPutRecordRequest);
        Mockito.verify(spyKinesisProducer).send(mockPutRecordRequest, 2);
        Mockito.verify(spyKinesisProducer).send(mockPutRecordRequest, 4);
    }

}