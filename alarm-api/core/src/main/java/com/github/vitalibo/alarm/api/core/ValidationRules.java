package com.github.vitalibo.alarm.api.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.vitalibo.alarm.api.core.model.CreateRuleRequest;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.util.ErrorState;
import com.github.vitalibo.alarm.api.core.util.Jackson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ValidationRules {

    private ValidationRules() {
    }

    public static void verifyJsonBody(HttpRequest request, ErrorState errorState) {
        String body = request.getBody();
        if (Objects.isNull(body) || body.isEmpty()) {
            errorState.addError(
                "body", "Required fields cannot be empty.");
            return;
        }

        try {
            Map<String, ?> json = Jackson.fromJsonString(body, new TypeReference<Map<String, ?>>() {});

            final List<String> known = Arrays.asList("metricName", "condition", "threshold");
            json.keySet().stream()
                .filter(key -> !known.contains(key))
                .forEach(key -> errorState.addError(
                    key, "Unknown body property."));

        } catch (Exception e) {
            errorState.addError(
                "body", "Rule must be in JSON format.");
        }
    }

    public static void verifyRuleId(HttpRequest request, ErrorState errorState) {
        Map<String, String> params = request.getQueryStringParameters();
        String value = params.get("ruleId");

        if (Objects.isNull(value) || value.isEmpty()) {
            errorState.addError(
                "ruleId", "Required fields cannot be empty.");
        }
    }

    public static void verifyMetricName(CreateRuleRequest request, ErrorState errorState) {
        String value = request.getMetricName();

        if (Objects.isNull(value) || value.isEmpty()) {
            errorState.addError(
                "metricName", "Required fields cannot be empty.");
        }
    }

    public static void verifyCondition(CreateRuleRequest request, ErrorState errorState) {
        Object value = request.getCondition();

        if (Objects.isNull(value)) {
            errorState.addError(
                "condition", "Required fields cannot be empty.");
        }
    }

    public static void verifyThreshold(CreateRuleRequest request, ErrorState errorState) {
        Double value = request.getThreshold();

        if (Objects.isNull(value)) {
            errorState.addError(
                "threshold", "Required fields cannot be empty.");
        }
    }

}