package com.github.vitalibo.alarm.processor.infrastructure.aws.rds;

import com.github.vitalibo.alarm.processor.core.model.Rule;
import com.github.vitalibo.alarm.processor.core.store.RuleStore;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@RequiredArgsConstructor
public class MySQLRuleStore implements RuleStore {

    private final Connection connection;

    @Override
    public List<Rule> getAll() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(
                Resources.asString("/mysql/GetAll.sql"));

            return RuleTranslator.from(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}