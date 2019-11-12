package com.github.vitalibo.alarm.processor.core.model.transform;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.IntNode;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeDeserializerTest {

    @Mock
    private JsonParser mockJsonParser;
    @Mock
    private ObjectCodec mockObjectCodec;
    @Mock
    private IntNode mockIntNode;
    @Mock
    private DeserializationContext mockDeserializationContext;

    private OffsetDateTimeDeserializer deserializer;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        deserializer = new OffsetDateTimeDeserializer();
    }

    @Test
    public void testDeserialize() throws IOException {
        Mockito.when(mockJsonParser.getCodec()).thenReturn(mockObjectCodec);
        Mockito.when(mockObjectCodec.readTree(mockJsonParser)).thenReturn(mockIntNode);
        Mockito.when(mockIntNode.asLong()).thenReturn(1536924044L);

        OffsetDateTime actual = deserializer.deserialize(mockJsonParser, mockDeserializationContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, OffsetDateTime.parse(
            "2018-09-14T11:20:44Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

}