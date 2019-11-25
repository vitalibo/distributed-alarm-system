package com.github.vitalibo.alarm.api.core.model;

import lombok.Data;

@Data
public class UpdateRuleResponse {

    private String metricName;
    private RuleCondition condition;
    private Double threshold;

    public UpdateRuleResponse withMetricName(String metricName) {
        this.metricName = metricName;
        return this;
    }

    public UpdateRuleResponse withCondition(RuleCondition condition) {
        this.condition = condition;
        return this;
    }

    public UpdateRuleResponse withThreshold(Double threshold) {
        this.threshold = threshold;
        return this;
    }

}