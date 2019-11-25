package com.github.vitalibo.alarm.api.core.facade;

import com.github.vitalibo.alarm.api.core.Facade;
import com.github.vitalibo.alarm.api.core.ValidationRules;
import com.github.vitalibo.alarm.api.core.model.*;
import com.github.vitalibo.alarm.api.core.model.transform.GetRuleRequestTranslator;
import com.github.vitalibo.alarm.api.core.model.transform.GetRuleResponseTranslator;
import com.github.vitalibo.alarm.api.core.store.AlarmStore;
import com.github.vitalibo.alarm.api.core.store.RuleStore;
import com.github.vitalibo.alarm.api.core.util.Rules;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class GetRuleFacade implements Facade<GetRuleRequest, GetRuleResponse> {

    private final Rules<GetRuleRequest> rules;
    private final AlarmStore alarmStore;
    private final RuleStore ruleStore;

    public GetRuleFacade(AlarmStore alarmStore, RuleStore ruleStore) {
        this(new Rules<>(
                Arrays.asList(
                    ValidationRules::verifyRuleId),
                Arrays.asList()),
            alarmStore, ruleStore);
    }

    @Override
    public HttpResponse<GetRuleResponse> process(HttpRequest request) {
        rules.verify(request);

        GetRuleResponse response = process(
            GetRuleRequestTranslator.from(request));

        return new HttpResponse<>(200, response);
    }

    @Override
    @SneakyThrows
    public GetRuleResponse process(GetRuleRequest request) {
        rules.verify(request);

        Future<Rule> rule = ruleStore.getRuleById(request.getRuleId());
        Future<Status> status = alarmStore.getStatusByRuleId(request.getRuleId());

        return GetRuleResponseTranslator.from(
            rule.get(), status.get());
    }

}