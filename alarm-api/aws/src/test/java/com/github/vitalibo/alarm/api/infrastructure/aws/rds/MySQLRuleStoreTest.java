package com.github.vitalibo.alarm.api.infrastructure.aws.rds;

import com.github.vitalibo.alarm.api.core.model.Rule;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Future;

public class MySQLRuleStoreTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private QueryTranslator mockQueryTranslator;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private Future mockFuture;

    private MySQLRuleStore spyRuleStore;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spyRuleStore = Mockito.spy(new MySQLRuleStore(
            mockConnection, mockQueryTranslator, mockQueryTranslator, mockQueryTranslator, mockQueryTranslator));
        Mockito.doAnswer(answer -> {
            MySQLRuleStore.Function<Statement, ?> function = answer.getArgument(0);
            Object result = function.apply(mockStatement);
            Mockito.when(mockFuture.get()).thenReturn(result);
            return mockFuture;
        }).when(spyRuleStore).supplyAsync(Mockito.any());
    }

    @Test
    public void testCreateRule() throws Exception {
        Mockito.when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true);
        Mockito.when(mockResultSet.getString(1)).thenReturn("12345");
        Rule rule = new Rule();
        rule.withMetricName("foo");
        Mockito.when(mockQueryTranslator.from(rule)).thenReturn("select field()");

        Future<String> actual = spyRuleStore.createRule(rule);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get(), "12345");
        Mockito.verify(mockStatement).execute("select field()", Statement.RETURN_GENERATED_KEYS);
        Mockito.verify(mockStatement).getGeneratedKeys();
    }

    @Test
    public void testUpdateRuleById() throws Exception {
        Mockito.when(mockStatement.executeQuery(Mockito.anyString())).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true, false);
        Mockito.when(mockResultSet.getString(1)).thenReturn("foo");
        Mockito.when(mockResultSet.getString(2)).thenReturn("LessThanThreshold");
        Mockito.when(mockResultSet.getDouble(3)).thenReturn(1.234);
        Rule rule = new Rule();
        rule.withMetricName("bar");
        Mockito.when(mockQueryTranslator.from(Mockito.any())).thenReturn("select field()", "select field(2)");

        Future<Rule> actual = spyRuleStore.updateRuleById("foo", rule);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get(), new Rule()
            .withMetricName("foo")
            .withCondition(RuleCondition.LessThanThreshold)
            .withThreshold(1.234));
        Mockito.verify(mockQueryTranslator).from(new HashMap<String, Object>() {{
            put("ruleId", "foo");
            put("rule", rule);
        }});
        Mockito.verify(mockQueryTranslator).from(Collections.singletonMap("ruleId", "foo"));
        Mockito.verify(mockStatement).execute("select field()");
        Mockito.verify(mockStatement).executeQuery("select field(2)");
    }

    @Test
    public void testDeleteRuleById() throws Exception {
        Mockito.when(mockQueryTranslator.from(Mockito.any())).thenReturn("select field()");

        Future<Void> actual = spyRuleStore.deleteRuleById("foo");

        Assert.assertNotNull(actual);
        Assert.assertNull(actual.get());
        Mockito.verify(mockQueryTranslator).from(Collections.singletonMap("ruleId", "foo"));
        Mockito.verify(mockStatement).execute("select field()");
    }

    @Test
    public void testGetRuleById() throws Exception {
        Mockito.when(mockQueryTranslator.from(Mockito.any())).thenReturn("select field()");
        Mockito.when(mockStatement.executeQuery(Mockito.anyString())).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true, false);
        Mockito.when(mockResultSet.getString(1)).thenReturn("foo");
        Mockito.when(mockResultSet.getString(2)).thenReturn("LessThanThreshold");
        Mockito.when(mockResultSet.getDouble(3)).thenReturn(1.234);

        Future<Rule> actual = spyRuleStore.getRuleById("foo");

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get(), new Rule()
            .withMetricName("foo")
            .withCondition(RuleCondition.LessThanThreshold)
            .withThreshold(1.234));
        Mockito.verify(mockQueryTranslator).from(Collections.singletonMap("ruleId", "foo"));
        Mockito.verify(mockStatement).executeQuery("select field()");
    }

    @Test
    public void testSupplyAsync() throws Exception {
        Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
        Object expected = new Object();

        Future<Object> actual = spyRuleStore.supplyAsync(o -> {
            Assert.assertEquals(o, mockStatement);
            return expected;
        });

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get(), expected);
    }

}