package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.transform;

import com.amazonaws.services.kinesis.model.Record;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import com.github.vitalibo.alarm.processor.infrastructure.aws.dms.DMSEvent;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.Collections;

public class DMSEventTranslatorTest {

    @Mock
    private Record mockRecord;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFromKinesisRecord() {
        Mockito.when(mockRecord.getData())
            .thenReturn(ByteBuffer.wrap(Resources.asString("/DMSEvent.json").getBytes()));

        DMSEvent actual = DMSEventTranslator.fromKinesisRecord(mockRecord);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getData(), Collections.singletonMap("foo", "bar"));
        DMSEvent.Metadata metadata = actual.getMetadata();
        Assert.assertEquals(metadata.getTimestamp(), "2019-05-15T00:51:38.301411Z");
        Assert.assertEquals(metadata.getRecordType(), "data");
        Assert.assertEquals(metadata.getOperation(), "insert");
        Assert.assertEquals(metadata.getPartitionKeyType(), "schema-table");
        Assert.assertEquals(metadata.getSchemaName(), "foo");
        Assert.assertEquals(metadata.getTableName(), "bar");
    }

}