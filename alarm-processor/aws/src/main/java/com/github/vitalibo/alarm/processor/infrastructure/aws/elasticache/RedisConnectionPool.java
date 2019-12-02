package com.github.vitalibo.alarm.processor.infrastructure.aws.elasticache;

import com.github.vitalibo.alarm.processor.core.util.function.Singleton;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.Utf8StringCodec;
import io.lettuce.core.masterslave.MasterSlave;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Wither;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Collections;
import java.util.Objects;

@RequiredArgsConstructor
public class RedisConnectionPool implements AutoCloseable {

    private final GenericObjectPool<StatefulRedisMasterSlaveConnection<String, String>> objectPool;

    @SneakyThrows
    public StatefulRedisMasterSlaveConnection<String, String> borrowObject() {
        return objectPool.borrowObject();
    }

    @SneakyThrows
    public StatefulRedisMasterSlaveConnection<String, String> borrowObject(long borrowMaxWaitMillis) {
        return objectPool.borrowObject(borrowMaxWaitMillis);
    }

    @Override
    public void close() {
        objectPool.close();
    }

    @Data
    @Wither
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder implements Singleton<RedisConnectionPool> {

        private final String endpoint;
        private final Integer port;
        private final Integer database;
        private final String password;

        public Builder() {
            this(null, 6379, 0, "");
        }

        public RedisConnectionPool build() {
            Objects.requireNonNull(endpoint, "endpoint");

            RedisURI.Builder redisUri = RedisURI.builder()
                .withHost(endpoint)
                .withPort(port)
                .withDatabase(database);

            if (Objects.nonNull(password) && !password.trim().isEmpty()) {
                redisUri.withPassword(password);
            }

            RedisClient client = RedisClient.create();
            client.setOptions(ClientOptions.builder()
                .autoReconnect(true)
                .pingBeforeActivateConnection(true)
                .build());

            return build(client, redisUri.build());
        }

        RedisConnectionPool build(RedisClient client, RedisURI uri) {
            GenericObjectPoolConfig configuration = new GenericObjectPoolConfig();
            GenericObjectPool<StatefulRedisMasterSlaveConnection<String, String>> objectPool =
                ConnectionPoolSupport.createGenericObjectPool(() -> {
                    StatefulRedisMasterSlaveConnection<String, String> connection =
                        MasterSlave.connect(client, new Utf8StringCodec(), Collections.singletonList(uri));
                    connection.setReadFrom(ReadFrom.MASTER_PREFERRED);
                    return connection;
                }, configuration);

            return build(objectPool);
        }

        RedisConnectionPool build(GenericObjectPool<StatefulRedisMasterSlaveConnection<String, String>> objectPool) {
            return new RedisConnectionPool(objectPool);
        }

        @Override
        public RedisConnectionPool get() {
            return build();
        }

    }

}