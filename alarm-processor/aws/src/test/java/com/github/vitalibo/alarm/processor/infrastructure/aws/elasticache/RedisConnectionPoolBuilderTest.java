package com.github.vitalibo.alarm.processor.infrastructure.aws.elasticache;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RedisConnectionPoolBuilderTest {

    @Mock
    private RedisConnectionPool mockRedisConnectionPool;
    @Captor
    private ArgumentCaptor<RedisClient> captorRedisClient;
    @Captor
    private ArgumentCaptor<RedisURI> captorRedisURI;
    @Mock
    private RedisClient mockRedisClient;
    @Mock
    private RedisURI mockRedisURI;
    @Captor
    private ArgumentCaptor<GenericObjectPool<StatefulRedisMasterSlaveConnection<String, String>>> captorGenericObjectPool;

    private RedisConnectionPool.Builder spyBuilder;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spyBuilder = Mockito.spy(new RedisConnectionPool.Builder()
            .withEndpoint("foo"));
    }

    @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = "endpoint")
    public void testBuildRequired() {
        new RedisConnectionPool.Builder()
            .build();
    }

    @Test
    public void testBuild() {
        Mockito.doReturn(mockRedisConnectionPool)
            .when(spyBuilder).build(Mockito.any(), Mockito.any());

        RedisConnectionPool actual = spyBuilder.build();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, mockRedisConnectionPool);
        Mockito.verify(spyBuilder).build(captorRedisClient.capture(), captorRedisURI.capture());
        RedisClient client = captorRedisClient.getValue();
        Assert.assertNotNull(client);
        RedisURI uri = captorRedisURI.getValue();
        Assert.assertNotNull(uri);
        Assert.assertEquals(uri.getHost(), "foo");
        Assert.assertEquals(uri.getPort(), 6379);
    }

    @Test
    public void testBuildGenericObjectPool() throws Exception {
        Mockito.doReturn(mockRedisConnectionPool)
            .when(spyBuilder).build(Mockito.any());

        RedisConnectionPool actual = spyBuilder.build(mockRedisClient, mockRedisURI);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, mockRedisConnectionPool);
        Mockito.verify(spyBuilder).build(captorGenericObjectPool.capture());
        Assert.assertNotNull(captorGenericObjectPool.getValue());
    }

    @Test
    public void testCreateOrGet() {
        Mockito.doReturn(mockRedisConnectionPool).when(spyBuilder).build();

        RedisConnectionPool actual = spyBuilder.createOrGet();

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, mockRedisConnectionPool);
        Mockito.verify(spyBuilder).build();
    }

}