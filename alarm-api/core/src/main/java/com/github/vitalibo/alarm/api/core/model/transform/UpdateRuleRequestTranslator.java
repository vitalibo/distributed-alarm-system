package com.github.vitalibo.alarm.api.core.model.transform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import com.github.vitalibo.alarm.api.core.model.UpdateRuleRequest;
import com.github.vitalibo.alarm.api.core.util.Jackson;

import java.util.Map;
import java.util.Objects;

public final class UpdateRuleRequestTranslator {

    private UpdateRuleRequestTranslator() {
    }

    public static UpdateRuleRequest from(HttpRequest request) {
        Map<String, String> param = request.getQueryStringParameters();
        Map<String, ?> body = Jackson.fromJsonString(
            request.getBody(), new TypeReference<Map<String, ?>>() {});

        return new UpdateRuleRequest()
            .withRuleId(param.get("ruleId"))
            .withMetricName((String) body.get("metricName"))
            .withCondition(Objects.nonNull(body.get("condition")) ?
                RuleCondition.valueOf((String) body.get("condition")) : null)
            .withThreshold((Double) body.get("threshold"));
    }

}