package com.github.vitalibo.alarm.api.infrastructure.aws.rds;

import com.github.vitalibo.alarm.api.core.model.Rule;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;

import java.sql.ResultSet;
import java.sql.SQLException;

final class RuleTranslator {

    private RuleTranslator() {
    }

    static Rule from(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return new Rule();
        }

        return new Rule()
            .withMetricName(resultSet.getString(1))
            .withCondition(RuleCondition.valueOf(resultSet.getString(2)))
            .withThreshold(resultSet.getDouble(3));
    }

}