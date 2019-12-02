package com.github.vitalibo.alarm.processor.infrastructure.aws.elasticache;

import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.store.AlarmStore;
import com.github.vitalibo.alarm.processor.core.util.function.Singleton;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RedisAlarmStore implements AlarmStore {

    private final Singleton<RedisConnectionPool> singletonRedisConnectionPool;

    @Override
    public void update(String ruleId, Alarm.State state) {
        RedisConnectionPool connectionPool = singletonRedisConnectionPool.createOrGet();
        try (StatefulRedisMasterSlaveConnection<String, String> connection = connectionPool.borrowObject()) {
            RedisAsyncCommands<String, String> async = connection.async();
            async.set(String.format("alarm:%s", ruleId), String.valueOf(state));
        }
    }

    @Override
    public void remove(String ruleId) {
        RedisConnectionPool connectionPool = singletonRedisConnectionPool.createOrGet();
        try (StatefulRedisMasterSlaveConnection<String, String> connection = connectionPool.borrowObject()) {
            RedisAsyncCommands<String, String> async = connection.async();
            async.del(String.format("alarm:%s", ruleId));
        }
    }

    @Override
    public List<Alarm> getAll() {
        RedisConnectionPool connectionPool = singletonRedisConnectionPool.createOrGet();
        try (StatefulRedisMasterSlaveConnection<String, String> connection = connectionPool.borrowObject()) {
            RedisCommands<String, String> sync = connection.sync();

            ScanArgs args = ScanArgs.Builder.matches(String.format("alarm:%s", "*")).limit(1_000);
            ScanCursor cursor = ScanCursor.INITIAL;
            List<String> keys = new ArrayList<>();
            while (!cursor.isFinished()) {
                KeyScanCursor<String> scan = sync.scan(cursor, args);

                keys.addAll(scan.getKeys());
                cursor = scan;
            }

            sync.multi();
            keys.forEach(sync::get);
            TransactionResult states = sync.exec();

            List<Alarm> list = new ArrayList<>();
            for (int i = 0; i < keys.size(); i++) {
                Alarm alarm = new Alarm();
                alarm.setRuleId(keys.get(i).split(":")[1]);
                alarm.setState(Alarm.State.valueOf(states.get(i)));
                list.add(alarm);
            }

            return list;
        }
    }

}