package com.github.vitalibo.alarm.api.infrastructure.aws.rds;

import com.github.vitalibo.alarm.api.core.model.Rule;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RuleTranslatorTest {

    @Mock
    private ResultSet mockResultSet;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFrom() throws SQLException {
        Mockito.when(mockResultSet.next()).thenReturn(true);
        Mockito.when(mockResultSet.getString(1)).thenReturn("foo");
        Mockito.when(mockResultSet.getString(2)).thenReturn("GreaterThanThreshold");
        Mockito.when(mockResultSet.getDouble(3)).thenReturn(1.23);

        Rule actual = RuleTranslator.from(mockResultSet);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getMetricName(), "foo");
        Assert.assertEquals(actual.getCondition(), RuleCondition.GreaterThanThreshold);
        Assert.assertEquals(actual.getThreshold(), 1.23);
    }

    @Test
    public void testFromEmpty() throws SQLException {
        Mockito.when(mockResultSet.next()).thenReturn(false);

        Rule actual = RuleTranslator.from(mockResultSet);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, new Rule());
    }

}