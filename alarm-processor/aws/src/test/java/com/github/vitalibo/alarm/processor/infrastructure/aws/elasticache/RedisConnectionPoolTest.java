package com.github.vitalibo.alarm.processor.infrastructure.aws.elasticache;

import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RedisConnectionPoolTest {

    @Mock
    private GenericObjectPool<StatefulRedisMasterSlaveConnection<String, String>> mockGenericObjectPool;

    private RedisConnectionPool connectionPool;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        connectionPool = new RedisConnectionPool(mockGenericObjectPool);
    }

    @Test
    public void testBorrowObject() throws Exception {
        connectionPool.borrowObject();

        Mockito.verify(mockGenericObjectPool).borrowObject();
    }

    @Test
    public void testBorrowObjectWithMaxWait() throws Exception {
        connectionPool.borrowObject(10);

        Mockito.verify(mockGenericObjectPool).borrowObject(10);
    }

    @Test
    public void testClose() {
        connectionPool.close();

        Mockito.verify(mockGenericObjectPool).close();
    }

}