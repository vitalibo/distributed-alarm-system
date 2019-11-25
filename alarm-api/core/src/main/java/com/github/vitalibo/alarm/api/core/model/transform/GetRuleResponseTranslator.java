package com.github.vitalibo.alarm.api.core.model.transform;

import com.github.vitalibo.alarm.api.core.model.GetRuleResponse;
import com.github.vitalibo.alarm.api.core.model.Rule;
import com.github.vitalibo.alarm.api.core.model.Status;

public final class GetRuleResponseTranslator {

    private GetRuleResponseTranslator() {
    }

    public static GetRuleResponse from(Rule rule, Status status) {
        return new GetRuleResponse()
            .withMetricName(rule.getMetricName())
            .withCondition(rule.getCondition())
            .withThreshold(rule.getThreshold())
            .withStatus(status);
    }

}