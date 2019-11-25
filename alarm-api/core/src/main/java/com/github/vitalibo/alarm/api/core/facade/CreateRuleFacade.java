package com.github.vitalibo.alarm.api.core.facade;

import com.github.vitalibo.alarm.api.core.Facade;
import com.github.vitalibo.alarm.api.core.ValidationRules;
import com.github.vitalibo.alarm.api.core.model.*;
import com.github.vitalibo.alarm.api.core.model.transform.CreateRuleRequestTranslator;
import com.github.vitalibo.alarm.api.core.store.RuleStore;
import com.github.vitalibo.alarm.api.core.util.Rules;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Arrays;

@RequiredArgsConstructor
public class CreateRuleFacade implements Facade<CreateRuleRequest, CreateRuleResponse> {

    private final Rules<CreateRuleRequest> rules;
    private final RuleStore ruleStore;

    public CreateRuleFacade(RuleStore ruleStore) {
        this(new Rules<>(
                Arrays.asList(
                    ValidationRules::verifyJsonBody),
                Arrays.asList(
                    ValidationRules::verifyMetricName,
                    ValidationRules::verifyCondition,
                    ValidationRules::verifyThreshold)),
            ruleStore);
    }

    @Override
    public HttpResponse<CreateRuleResponse> process(HttpRequest request) {
        rules.verify(request);

        CreateRuleResponse response = process(
            CreateRuleRequestTranslator.from(request));

        return new HttpResponse<>(200, response);
    }

    @Override
    @SneakyThrows
    public CreateRuleResponse process(CreateRuleRequest request) {
        rules.verify(request);

        Rule rule = new Rule()
            .withMetricName(request.getMetricName())
            .withCondition(request.getCondition())
            .withThreshold(request.getThreshold());

        String ruleId = ruleStore.createRule(rule)
            .get();

        return new CreateRuleResponse()
            .withRuleId(ruleId);
    }

}