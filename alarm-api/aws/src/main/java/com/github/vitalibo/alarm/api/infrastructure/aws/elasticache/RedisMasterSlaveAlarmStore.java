package com.github.vitalibo.alarm.api.infrastructure.aws.elasticache;

import com.github.vitalibo.alarm.api.core.model.Status;
import com.github.vitalibo.alarm.api.core.store.AlarmStore;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class RedisMasterSlaveAlarmStore implements AlarmStore {

    private final StatefulRedisMasterSlaveConnection<String, String> connection;

    @Override
    public Future<Status> getStatusByRuleId(String ruleId) {
        return connection.async()
            .get(String.format("alarm:%s", ruleId))
            .thenApply(o -> Optional.ofNullable(o)
                .map(Status::valueOf)
                .orElse(null))
            .toCompletableFuture();
    }

}