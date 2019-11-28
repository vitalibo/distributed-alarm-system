package com.github.vitalibo.alarm.api.infrastructure.aws.elasticache;

import com.github.vitalibo.alarm.api.core.model.Status;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RedisMasterSlaveAlarmStoreTest {

    @Mock
    private StatefulRedisMasterSlaveConnection<String, String> mockStatefulRedisMasterSlaveConnection;
    @Mock
    private RedisAsyncCommands<String, String> mockRedisAsyncCommands;

    private RedisMasterSlaveAlarmStore alarmStore;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        alarmStore = new RedisMasterSlaveAlarmStore(mockStatefulRedisMasterSlaveConnection);
    }

    @Test
    public void testGetStatusByRuleId() throws ExecutionException, InterruptedException {
        Mockito.when(mockStatefulRedisMasterSlaveConnection.async()).thenReturn(mockRedisAsyncCommands);
        Mockito.when(mockRedisAsyncCommands.get(Mockito.anyString())).thenReturn(new RedisFutureStub<>(() -> "Ok"));

        Future<Status> actual = alarmStore.getStatusByRuleId("foo");

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.get(), Status.Ok);
        Mockito.verify(mockStatefulRedisMasterSlaveConnection).async();
        Mockito.verify(mockRedisAsyncCommands).get("alarm:foo");
    }

    @RequiredArgsConstructor
    private static class RedisFutureStub<T> implements RedisFuture<T> {

        @Delegate
        private final CompletableFuture<T> completableFuture;

        RedisFutureStub(Supplier<T> supplier) {
            this.completableFuture = CompletableFuture.supplyAsync(supplier);
        }

        @Override
        public String getError() {
            return null;
        }

        @Override
        public boolean await(long timeout, TimeUnit unit) {
            return false;
        }

    }

}