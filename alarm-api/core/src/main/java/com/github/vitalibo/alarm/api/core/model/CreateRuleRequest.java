package com.github.vitalibo.alarm.api.core.model;

import lombok.Data;

@Data
public class CreateRuleRequest {

    private String metricName;
    private RuleCondition condition;
    private Double threshold;

    public CreateRuleRequest withMetricName(String metricName) {
        this.metricName = metricName;
        return this;
    }

    public CreateRuleRequest withCondition(RuleCondition condition) {
        this.condition = condition;
        return this;
    }

    public CreateRuleRequest withThreshold(Double threshold) {
        this.threshold = threshold;
        return this;
    }

}