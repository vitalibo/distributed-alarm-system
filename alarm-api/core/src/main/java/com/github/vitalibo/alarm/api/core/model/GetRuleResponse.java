package com.github.vitalibo.alarm.api.core.model;

import lombok.Data;

@Data
public class GetRuleResponse {

    private String ruleId;
    private String metricName;
    private RuleCondition condition;
    private Double threshold;
    private Status status;

    public GetRuleResponse withRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public GetRuleResponse withMetricName(String metricName) {
        this.metricName = metricName;
        return this;
    }

    public GetRuleResponse withCondition(RuleCondition condition) {
        this.condition = condition;
        return this;
    }

    public GetRuleResponse withThreshold(Double threshold) {
        this.threshold = threshold;
        return this;
    }

    public GetRuleResponse withStatus(Status status) {
        this.status = status;
        return this;
    }

}