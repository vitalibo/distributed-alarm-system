package com.github.vitalibo.alarm.processor.infrastructure.aws.rds;

import com.github.vitalibo.alarm.processor.core.model.Rule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

final class RuleTranslator {

    private RuleTranslator() {
    }

    static List<Rule> from(ResultSet resultSet) throws SQLException {
        List<Rule> rules = new ArrayList<>();
        while (resultSet.next()) {
            rules.add(parseFrom(resultSet));
        }

        return rules;
    }

    static Rule parseFrom(ResultSet resultSet) throws SQLException {
        return new Rule()
            .withId(resultSet.getString(1))
            .withMetricName(resultSet.getString(2))
            .withCondition(Rule.Condition.valueOf(resultSet.getString(3)))
            .withThreshold(resultSet.getDouble(4));
    }

}