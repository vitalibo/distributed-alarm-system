package com.github.vitalibo.alarm.api.core.model.transform;

import com.github.vitalibo.alarm.api.core.model.GetRuleRequest;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;

import java.util.Map;

public final class GetRuleRequestTranslator {

    private GetRuleRequestTranslator() {
    }

    public static GetRuleRequest from(HttpRequest request) {
        Map<String, String> param = request.getQueryStringParameters();
        return new GetRuleRequest()
            .withRuleId(param.get("ruleId"));
    }

}
