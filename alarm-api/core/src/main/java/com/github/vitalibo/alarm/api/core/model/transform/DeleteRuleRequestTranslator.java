package com.github.vitalibo.alarm.api.core.model.transform;

import com.github.vitalibo.alarm.api.core.model.DeleteRuleRequest;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;

import java.util.Map;

public final class DeleteRuleRequestTranslator {

    private DeleteRuleRequestTranslator() {
    }

    public static DeleteRuleRequest from(HttpRequest request) {
        Map<String, String> param = request.getQueryStringParameters();
        return new DeleteRuleRequest()
            .withRuleId(param.get("ruleId"));
    }

}