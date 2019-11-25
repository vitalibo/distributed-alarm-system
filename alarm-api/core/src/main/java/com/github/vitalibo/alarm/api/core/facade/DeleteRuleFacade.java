package com.github.vitalibo.alarm.api.core.facade;

import com.github.vitalibo.alarm.api.core.Facade;
import com.github.vitalibo.alarm.api.core.ValidationRules;
import com.github.vitalibo.alarm.api.core.model.DeleteRuleRequest;
import com.github.vitalibo.alarm.api.core.model.DeleteRuleResponse;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.HttpResponse;
import com.github.vitalibo.alarm.api.core.model.transform.DeleteRuleRequestTranslator;
import com.github.vitalibo.alarm.api.core.store.RuleStore;
import com.github.vitalibo.alarm.api.core.util.Rules;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Arrays;

@RequiredArgsConstructor
public class DeleteRuleFacade implements Facade<DeleteRuleRequest, DeleteRuleResponse> {

    private final Rules<DeleteRuleRequest> rules;
    private final RuleStore ruleStore;

    public DeleteRuleFacade(RuleStore ruleStore) {
        this(new Rules<>(
                Arrays.asList(
                    ValidationRules::verifyRuleId),
                Arrays.asList()),
            ruleStore);
    }

    @Override
    public HttpResponse<DeleteRuleResponse> process(HttpRequest request) {
        rules.verify(request);

        DeleteRuleResponse response = process(
            DeleteRuleRequestTranslator.from(request));

        return new HttpResponse<>(200, response);
    }

    @Override
    @SneakyThrows
    public DeleteRuleResponse process(DeleteRuleRequest request) {
        rules.verify(request);

        ruleStore.deleteRuleById(request.getRuleId());

        return new DeleteRuleResponse();
    }

}