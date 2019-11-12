package com.github.vitalibo.alarm.processor.core.model.transform;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeSerializerTest {

    @Mock
    private JsonGenerator mockJsonGenerator;
    @Mock
    private SerializerProvider mockSerializerProvider;

    private OffsetDateTimeSerializer serializer;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        serializer = new OffsetDateTimeSerializer();
    }

    @Test
    public void testSerialize() throws IOException {
        OffsetDateTime sample = OffsetDateTime.parse(
            "2018-09-14T11:20:44Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        serializer.serialize(sample, mockJsonGenerator, mockSerializerProvider);

        Mockito.verify(mockJsonGenerator).writeNumber(1536924044L);
    }

}