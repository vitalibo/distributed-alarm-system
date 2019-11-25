package com.github.vitalibo.alarm.api.core.model;

import lombok.Data;

@Data
public class GetRuleRequest {

    private String ruleId;

    public GetRuleRequest withRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

}