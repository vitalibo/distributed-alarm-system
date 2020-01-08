package com.github.vitalibo.alarm.api.infrastructure.azure.cosmosdb;

import com.github.vitalibo.alarm.api.core.model.Rule;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import com.microsoft.azure.documentdb.Document;

import java.util.Objects;
import java.util.Optional;

class RuleTranslator {

    private RuleTranslator() {
    }

    static Rule from(Document document) {
        if (Objects.nonNull(document.getTimeToLive()) && document.getTimeToLive() > 0) {
            return new Rule();
        }

        return new Rule()
            .withRuleId(document.getId())
            .withMetricName(document.getString("metricName"))
            .withCondition(Optional.ofNullable(document.getString("condition"))
                .map(RuleCondition::valueOf)
                .orElse(null))
            .withThreshold(document.getDouble("threshold"));
    }

}