package com.github.vitalibo.alarm.api.core.model;

import lombok.Data;

@Data
public class DeleteRuleRequest {

    private String ruleId;

    public DeleteRuleRequest withRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

}