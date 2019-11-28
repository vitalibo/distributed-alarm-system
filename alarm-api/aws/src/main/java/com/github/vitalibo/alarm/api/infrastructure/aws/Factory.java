package com.github.vitalibo.alarm.api.infrastructure.aws;

import com.github.vitalibo.alarm.api.core.Facade;
import com.github.vitalibo.alarm.api.core.facade.CreateRuleFacade;
import com.github.vitalibo.alarm.api.core.facade.DeleteRuleFacade;
import com.github.vitalibo.alarm.api.core.facade.GetRuleFacade;
import com.github.vitalibo.alarm.api.core.facade.UpdateRuleFacade;
import com.github.vitalibo.alarm.api.core.model.*;
import com.github.vitalibo.alarm.api.core.store.AlarmStore;
import com.github.vitalibo.alarm.api.core.store.RuleStore;
import com.github.vitalibo.alarm.api.infrastructure.aws.elasticache.RedisMasterSlaveAlarmStore;
import com.github.vitalibo.alarm.api.infrastructure.aws.rds.MySQLRuleStore;
import com.github.vitalibo.alarm.api.infrastructure.aws.rds.QueryTranslator;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.Utf8StringCodec;
import io.lettuce.core.masterslave.MasterSlave;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Collections;

public class Factory {

    private static final String MYSQL_HOST = "aws.rds.mysql.host";
    private static final String MYSQL_PORT = "aws.rds.mysql.port";
    private static final String MYSQL_DATABASE = "aws.rds.mysql.database";
    private static final String MYSQL_USER = "aws.rds.mysql.user";
    private static final String MYSQL_PASSWORD = "aws.rds.mysql.password";
    private static final String REDIS_HOST = "aws.elasticache.redis.host";
    private static final String REDIS_PORT = "aws.elasticache.redis.port";
    private static final String REDIS_DATABASE = "aws.elasticache.redis.database";
    private static final String REDIS_PASSWORD = "aws.elasticache.redis.password";

    @Getter(lazy = true)
    private static final Factory instance = new Factory(ConfigFactory.load(), ConfigFactory.parseResources("default-application.conf"));

    private final Config config;
    private final RuleStore ruleStore;
    private final AlarmStore alarmStore;
    private final Configuration freemarkerConfiguration;

    Factory(Config... configs) {
        this.config = Arrays.stream(configs)
            .reduce(Config::withFallback)
            .orElseThrow(IllegalStateException::new)
            .resolve();
        this.freemarkerConfiguration = createConfiguration();
        this.ruleStore = createMySQLRuleStore();
        this.alarmStore = createRedisAlarmStore();
    }

    public Facade<CreateRuleRequest, CreateRuleResponse> createCreateRuleFacade() {
        return new CreateRuleFacade(
            ruleStore);
    }

    public Facade<DeleteRuleRequest, DeleteRuleResponse> createDeleteRuleFacade() {
        return new DeleteRuleFacade(
            ruleStore);
    }

    public Facade<GetRuleRequest, GetRuleResponse> createGetRuleFacade() {
        return new GetRuleFacade(
            alarmStore,
            ruleStore);
    }

    public Facade<UpdateRuleRequest, UpdateRuleResponse> createUpdateRuleFacade() {
        return new UpdateRuleFacade(
            ruleStore);
    }

    private AlarmStore createRedisAlarmStore() {
        RedisClient client = RedisClient.create();
        client.setOptions(ClientOptions.builder()
            .autoReconnect(true)
            .pingBeforeActivateConnection(true)
            .build());

        RedisURI.Builder redisUri = RedisURI.builder()
            .withHost(config.getString(REDIS_HOST))
            .withPort(config.getInt(REDIS_PORT))
            .withDatabase(config.getInt(REDIS_DATABASE));

        if (config.hasPath(REDIS_PASSWORD)) {
            redisUri.withPassword(config.getString(REDIS_PASSWORD));
        }

        StatefulRedisMasterSlaveConnection<String, String> connection =
            MasterSlave.connect(client, new Utf8StringCodec(), Collections.singletonList(redisUri.build()));
        connection.setReadFrom(ReadFrom.MASTER_PREFERRED);
        return new RedisMasterSlaveAlarmStore(connection);
    }

    @SneakyThrows
    private RuleStore createMySQLRuleStore() {
        Class.forName("com.mysql.jdbc.Driver");

        Connection connection = DriverManager.getConnection(
            String.format("jdbc:mysql://%s:%s/%s",
                config.getString(MYSQL_HOST), config.getInt(MYSQL_PORT), config.getString(MYSQL_DATABASE)),
            config.getString(MYSQL_USER), config.getString(MYSQL_PASSWORD));

        return new MySQLRuleStore(
            connection,
            makeQueryStringTranslator("CreateRule"),
            makeQueryStringTranslator("UpdateRuleById"),
            makeQueryStringTranslator("DeleteRuleById"),
            makeQueryStringTranslator("GetRuleById"));
    }

    @SneakyThrows
    private QueryTranslator makeQueryStringTranslator(String resource) {
        return new QueryTranslator(
            freemarkerConfiguration.getTemplate(
                String.format("mysql/%s.ftl", resource)));
    }

    private static Configuration createConfiguration() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        cfg.setClassForTemplateLoading(Factory.class, "/");
        cfg.setDefaultEncoding(StandardCharsets.UTF_8.name());
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        return cfg;
    }

}