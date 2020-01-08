package com.github.vitalibo.alarm.api.core.model.transform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.vitalibo.alarm.api.core.model.CreateRuleRequest;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import com.github.vitalibo.alarm.api.core.util.Jackson;

import java.util.Map;
import java.util.Optional;

public final class CreateRuleRequestTranslator {

    private CreateRuleRequestTranslator() {
    }

    public static CreateRuleRequest from(HttpRequest request) {
        Map<String, ?> body = Jackson.fromJsonString(
            request.getBody(), new TypeReference<Map<String, ?>>() {});

        return new CreateRuleRequest()
            .withMetricName((String) body.get("metricName"))
            .withCondition(Optional.ofNullable((String) body.get("condition"))
                .map(RuleCondition::valueOf)
                .orElse(null))
            .withThreshold((Double) body.get("threshold"));
    }

}