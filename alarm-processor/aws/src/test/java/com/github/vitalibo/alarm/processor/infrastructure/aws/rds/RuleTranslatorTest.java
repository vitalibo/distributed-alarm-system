package com.github.vitalibo.alarm.processor.infrastructure.aws.rds;

import com.github.vitalibo.alarm.processor.core.model.Rule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.util.List;

public class RuleTranslatorTest {

    @Mock
    private ResultSet mockResultSet;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFrom() throws Exception {
        Mockito.when(mockResultSet.next()).thenReturn(true, true, false);
        Mockito.when(mockResultSet.getString(1)).thenReturn("foo");
        Mockito.when(mockResultSet.getString(2)).thenReturn("bar");
        Mockito.when(mockResultSet.getString(3)).thenReturn("GreaterThanThreshold");
        Mockito.when(mockResultSet.getDouble(4)).thenReturn(1.23);

        List<Rule> actual = RuleTranslator.from(mockResultSet);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.size(), 2);
        Rule rule = actual.get(0);
        Assert.assertEquals(rule.getId(), "foo");
        Assert.assertEquals(rule.getMetricName(), "bar");
        Assert.assertEquals(rule.getCondition(), Rule.Condition.GreaterThanThreshold);
        Assert.assertEquals(rule.getThreshold(), 1.23);
    }

}