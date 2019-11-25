package com.github.vitalibo.alarm.api.core.model;

import lombok.Data;

@Data
public class UpdateRuleRequest {

    private String ruleId;
    private String metricName;
    private RuleCondition condition;
    private Double threshold;

    public UpdateRuleRequest withRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public UpdateRuleRequest withMetricName(String metricName) {
        this.metricName = metricName;
        return this;
    }

    public UpdateRuleRequest withCondition(RuleCondition condition) {
        this.condition = condition;
        return this;
    }

    public UpdateRuleRequest withThreshold(Double threshold) {
        this.threshold = threshold;
        return this;
    }

}