package com.github.vitalibo.alarm.processor.core.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Alarm implements Serializable {

    private String metricName;
    private String ruleId;
    private State state;

    public Alarm withMetricName(String metricName) {
        this.metricName = metricName;
        return this;
    }

    public Alarm withRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public Alarm withState(State state) {
        this.state = state;
        return this;
    }

    public enum State implements Serializable {

        Ok,
        Pending,
        Alarm

    }

}