package com.github.vitalibo.alarm.api.infrastructure.azure.cosmosdb;

import com.github.vitalibo.alarm.api.core.model.Rule;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import com.microsoft.azure.documentdb.Document;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RuleTranslatorTest {

    @Mock
    private Document mockDocument;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFrom() {
        Mockito.when(mockDocument.getId()).thenReturn("foo");
        Mockito.when(mockDocument.getString("metricName")).thenReturn("bar");
        Mockito.when(mockDocument.getString("condition")).thenReturn("LessThanThreshold");
        Mockito.when(mockDocument.getDouble("threshold")).thenReturn(1.23);

        Rule actual = RuleTranslator.from(mockDocument);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getRuleId(), "foo");
        Assert.assertEquals(actual.getMetricName(), "bar");
        Assert.assertEquals(actual.getCondition(), RuleCondition.LessThanThreshold);
        Assert.assertEquals(actual.getThreshold(), 1.23);
    }

    @Test
    public void testFromWithTTL() {
        Mockito.when(mockDocument.getId()).thenReturn("foo");
        Mockito.when(mockDocument.getString("metricName")).thenReturn("bar");
        Mockito.when(mockDocument.getString("condition")).thenReturn("LessThanThreshold");
        Mockito.when(mockDocument.getDouble("threshold")).thenReturn(1.23);
        Mockito.when(mockDocument.getTimeToLive()).thenReturn(1);

        Rule actual = RuleTranslator.from(mockDocument);

        Assert.assertNotNull(actual);
        Assert.assertNull(actual.getRuleId());
        Assert.assertNull(actual.getMetricName());
        Assert.assertNull(actual.getCondition());
        Assert.assertNull(actual.getThreshold());
    }

}