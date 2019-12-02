package com.github.vitalibo.alarm.processor.infrastructure.aws.elasticache;

import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.util.function.Singleton;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class RedisAlarmStoreTest {

    @Mock
    private Singleton<RedisConnectionPool> mockSingleton;
    @Mock
    private RedisConnectionPool mockRedisConnectionPool;
    @Mock
    private StatefulRedisMasterSlaveConnection<String, String> mockStatefulRedisMasterSlaveConnection;
    @Mock
    private RedisAsyncCommands<String, String> mockRedisAsyncCommands;
    @Mock
    private RedisCommands<String, String> mockRedisCommands;
    @Mock
    private KeyScanCursor<String> mockKeyScanCursor;
    @Captor
    private ArgumentCaptor<ScanArgs> captorScanArgs;
    @Mock
    private TransactionResult mockTransactionResult;

    private RedisAlarmStore alarmStore;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        alarmStore = new RedisAlarmStore(mockSingleton);
        Mockito.when(mockSingleton.createOrGet()).thenReturn(mockRedisConnectionPool);
        Mockito.when(mockRedisConnectionPool.borrowObject()).thenReturn(mockStatefulRedisMasterSlaveConnection);
        Mockito.when(mockStatefulRedisMasterSlaveConnection.async()).thenReturn(mockRedisAsyncCommands);
        Mockito.when(mockStatefulRedisMasterSlaveConnection.sync()).thenReturn(mockRedisCommands);
    }

    @Test
    public void testUpdate() {
        alarmStore.update("foo", Alarm.State.Ok);

        Mockito.verify(mockSingleton).createOrGet();
        Mockito.verify(mockRedisConnectionPool).borrowObject();
        Mockito.verify(mockStatefulRedisMasterSlaveConnection).async();
        Mockito.verify(mockRedisAsyncCommands).set("alarm:foo", "Ok");
    }

    @Test
    public void testRemove() {
        alarmStore.remove("foo");

        Mockito.verify(mockSingleton).createOrGet();
        Mockito.verify(mockRedisConnectionPool).borrowObject();
        Mockito.verify(mockStatefulRedisMasterSlaveConnection).async();
        Mockito.verify(mockRedisAsyncCommands).del("alarm:foo");
    }

    @Test
    public void testGetAll() {
        Mockito.when(mockRedisCommands.scan(Mockito.any(ScanCursor.class), Mockito.any(ScanArgs.class)))
            .thenReturn(mockKeyScanCursor);
        Mockito.when(mockKeyScanCursor.isFinished()).thenReturn(false, true);
        Mockito.when(mockKeyScanCursor.getKeys())
            .thenReturn(Arrays.asList("alarm:1", "alarm:2", "alarm:3"))
            .thenReturn(Arrays.asList("alarm:4", "alarm:5"));
        Mockito.when(mockRedisCommands.exec()).thenReturn(mockTransactionResult);
        Mockito.when(mockTransactionResult.get(Mockito.anyInt()))
            .thenReturn("Ok", "Ok", "Alarm", "Pending", "Alarm");

        List<Alarm> actual = alarmStore.getAll();

        Mockito.verify(mockSingleton).createOrGet();
        Mockito.verify(mockRedisConnectionPool).borrowObject();
        Mockito.verify(mockStatefulRedisMasterSlaveConnection).sync();
        Mockito.verify(mockKeyScanCursor, Mockito.times(2)).getKeys();
        Mockito.verify(mockKeyScanCursor, Mockito.times(2)).isFinished();
        Mockito.verify(mockRedisCommands).scan(Mockito.eq(ScanCursor.INITIAL), captorScanArgs.capture());
        Mockito.verify(mockRedisCommands).scan(mockKeyScanCursor, captorScanArgs.getValue());
        Mockito.verify(mockRedisCommands).multi();
        Mockito.verify(mockRedisCommands).get("alarm:1");
        Mockito.verify(mockRedisCommands).get("alarm:5");
        Mockito.verify(mockRedisCommands).exec();
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.size(), 5);
        Assert.assertEquals(actual.get(0).getRuleId(), "1");
        Assert.assertEquals(actual.get(0).getState(), Alarm.State.Ok);
        Assert.assertEquals(actual.get(1).getRuleId(), "2");
        Assert.assertEquals(actual.get(1).getState(), Alarm.State.Ok);
        Assert.assertEquals(actual.get(2).getRuleId(), "3");
        Assert.assertEquals(actual.get(2).getState(), Alarm.State.Alarm);
        Assert.assertEquals(actual.get(3).getRuleId(), "4");
        Assert.assertEquals(actual.get(3).getState(), Alarm.State.Pending);
        Assert.assertEquals(actual.get(4).getRuleId(), "5");
        Assert.assertEquals(actual.get(4).getState(), Alarm.State.Alarm);
    }

}