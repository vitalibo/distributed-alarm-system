package com.github.vitalibo.alarm.processor.core.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Rule implements Serializable {

    private String id;
    private String metricName;
    private Condition condition;
    private Double threshold;

    public Rule withId(String id) {
        this.id = id;
        return this;
    }

    public Rule withMetricName(String metricName) {
        this.metricName = metricName;
        return this;
    }

    public Rule withCondition(Condition condition) {
        this.condition = condition;
        return this;
    }

    public Rule withThreshold(Double threshold) {
        this.threshold = threshold;
        return this;
    }

    public enum Condition implements Serializable {

        GreaterThanOrEqualToThreshold,
        GreaterThanThreshold,
        LessThanOrEqualToThreshold,
        LessThanThreshold

    }

}