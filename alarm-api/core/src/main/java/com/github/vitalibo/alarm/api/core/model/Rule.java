package com.github.vitalibo.alarm.api.core.model;

import lombok.Data;

@Data
public class Rule {

    private String ruleId;
    private String metricName;
    private RuleCondition condition;
    private Double threshold;

    public Rule withRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public Rule withMetricName(String metricName) {
        this.metricName = metricName;
        return this;
    }

    public Rule withCondition(RuleCondition condition) {
        this.condition = condition;
        return this;
    }

    public Rule withThreshold(Double threshold) {
        this.threshold = threshold;
        return this;
    }

}