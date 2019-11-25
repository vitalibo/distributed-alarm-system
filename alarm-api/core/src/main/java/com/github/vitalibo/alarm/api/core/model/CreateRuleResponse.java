package com.github.vitalibo.alarm.api.core.model;

import lombok.Data;

@Data
public class CreateRuleResponse {

    private String ruleId;

    public CreateRuleResponse withRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

}