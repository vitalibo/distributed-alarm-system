package com.github.vitalibo.alarm.api.infrastructure.aws.rds;

import com.github.vitalibo.alarm.api.core.model.Rule;
import com.github.vitalibo.alarm.api.core.store.RuleStore;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class MySQLRuleStore implements RuleStore {

    private final Connection connection;

    private final QueryTranslator createRuleQuery;
    private final QueryTranslator updateRuleByIdQuery;
    private final QueryTranslator deleteRuleByIdQuery;
    private final QueryTranslator getRuleByIdQuery;

    @Override
    public Future<String> createRule(Rule rule) {
        return supplyAsync(statement -> {
            statement.execute(createRuleQuery.from(rule), Statement.RETURN_GENERATED_KEYS);

            ResultSet resultSet = statement.getGeneratedKeys();
            if (!resultSet.next()) {
                throw new RuntimeException("Can't get generated keys.");
            }

            return resultSet.getString(1);
        });
    }

    @Override
    public Future<Rule> updateRuleById(String ruleId, Rule rule) {
        return supplyAsync(statement -> {
            statement.execute(updateRuleByIdQuery.from(new HashMap<String, Object>() {{
                put("ruleId", ruleId);
                put("rule", rule);
            }}));

            ResultSet resultSet = statement.executeQuery(
                getRuleByIdQuery.from(Collections.singletonMap("ruleId", ruleId)));

            return RuleTranslator.from(resultSet);
        });
    }

    @Override
    public Future<Void> deleteRuleById(String ruleId) {
        return supplyAsync(statement -> {
            statement.execute(deleteRuleByIdQuery.from(Collections.singletonMap("ruleId", ruleId)));
            return null;
        });
    }

    @Override
    public Future<Rule> getRuleById(String ruleId) {
        return supplyAsync(statement -> {
            ResultSet resultSet = statement.executeQuery(
                getRuleByIdQuery.from(Collections.singletonMap("ruleId", ruleId)));

            return RuleTranslator.from(resultSet);
        });
    }

    <T> Future<T> supplyAsync(Function<Statement, T> function) {
        return CompletableFuture.supplyAsync(() -> {
            try (Statement statement = connection.createStatement()) {
                return function.apply(statement);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    @FunctionalInterface
    interface Function<T, R> {

        R apply(T t) throws Exception;

    }

}