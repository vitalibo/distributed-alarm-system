package com.github.vitalibo.alarm.subject.core.value.type.timestamp;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.function.Supplier;

public class NaturalGrowthTimestampValueGeneratorTest {

    @Mock
    private Supplier<Long> mockSupplier;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerate() {
        OffsetDateTime expected = OffsetDateTime.parse("2019-01-01T00:00:00Z");
        Mockito.when(mockSupplier.get()).thenReturn(60_000L, 120_000L);
        NaturalGrowthTimestampValueGenerator generator = new NaturalGrowthTimestampValueGenerator(expected, mockSupplier);

        OffsetDateTime actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, expected.plusMinutes(1));

        actual = generator.generate();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, expected.plusMinutes(2));
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "`startAt` must be defined.")
    public void testFailCreateStartAtIsNull() {
        new NaturalGrowthTimestampValueGenerator(null);
    }

}