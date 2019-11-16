package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.transform;

import com.amazonaws.services.kinesis.model.Record;
import com.github.vitalibo.alarm.processor.core.model.Metric;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class MetricTranslatorTest {

    @Mock
    private Record mockRecord;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFromKinesisRecord() {
        Mockito.when(mockRecord.getData())
            .thenReturn(ByteBuffer.wrap(Resources.asString("/Metric.json").getBytes()));

        Metric actual = MetricTranslator.fromKinesisRecord(mockRecord);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "foo");
        Assert.assertEquals(actual.getTimestamp(), OffsetDateTime.of(2019, 11, 11, 21, 11, 23, 0, ZoneOffset.UTC));
        Assert.assertEquals(actual.getValue(), 1.24);
    }

}