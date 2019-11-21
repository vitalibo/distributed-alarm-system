package com.github.vitalibo.alarm.subject.core.value.type;

import com.github.vitalibo.alarm.subject.core.GenericValue;
import com.github.vitalibo.alarm.subject.core.value.type.SchemaValueGenerator.Schema;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;

public class SchemaValueGeneratorTest {

    @Mock
    private GenericValue mockGenericValue;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerate() {
        Schema schema = new Schema();
        schema.put("foo", mockGenericValue);
        Mockito.when(mockGenericValue.get()).thenReturn(1);
        SchemaValueGenerator generator = new SchemaValueGenerator(schema);

        Map<String, ?> actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, Collections.singletonMap("foo", 1));
    }

}