package com.github.vitalibo.alarm.processor.infrastructure.aws.rds;

import com.github.vitalibo.alarm.processor.core.model.Rule;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class MySQLRuleStoreTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private ResultSet mockResultSet;

    private MySQLRuleStore ruleStore;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ruleStore = new MySQLRuleStore(mockConnection);
    }

    @Test
    public void testGetAll() throws Exception {
        Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
        Mockito.when(mockStatement.executeQuery(Mockito.anyString())).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(false);

        List<Rule> actual = ruleStore.getAll();

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isEmpty());
        Mockito.verify(mockConnection).createStatement();
        Mockito.verify(mockStatement).executeQuery(Resources.asString("/mysql/GetAll.sql"));
    }

}