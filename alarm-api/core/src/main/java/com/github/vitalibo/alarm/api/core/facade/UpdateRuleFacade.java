package com.github.vitalibo.alarm.api.core.facade;

import com.github.vitalibo.alarm.api.core.Facade;
import com.github.vitalibo.alarm.api.core.ValidationRules;
import com.github.vitalibo.alarm.api.core.model.*;
import com.github.vitalibo.alarm.api.core.model.transform.UpdateRuleRequestTranslator;
import com.github.vitalibo.alarm.api.core.store.RuleStore;
import com.github.vitalibo.alarm.api.core.util.Rules;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Arrays;

@RequiredArgsConstructor
public class UpdateRuleFacade implements Facade<UpdateRuleRequest, UpdateRuleResponse> {

    private final Rules<UpdateRuleRequest> rules;
    private final RuleStore ruleStore;

    public UpdateRuleFacade(RuleStore ruleStore) {
        this(new Rules<>(
                Arrays.asList(
                    ValidationRules::verifyRuleId,
                    ValidationRules::verifyJsonBody),
                Arrays.asList()),
            ruleStore);
    }

    @Override
    public HttpResponse<UpdateRuleResponse> process(HttpRequest request) {
        rules.verify(request);

        UpdateRuleResponse response = process(
            UpdateRuleRequestTranslator.from(request));

        return new HttpResponse<>(200, response);
    }

    @Override
    @SneakyThrows
    public UpdateRuleResponse process(UpdateRuleRequest request) {
        rules.verify(request);

        Rule rule = new Rule()
            .withMetricName(request.getMetricName())
            .withCondition(request.getCondition())
            .withThreshold(request.getThreshold());

        Rule result = ruleStore.updateRuleById(request.getRuleId(), rule)
            .get();

        return new UpdateRuleResponse()
            .withMetricName(result.getMetricName())
            .withCondition(result.getCondition())
            .withThreshold(result.getThreshold());
    }

}