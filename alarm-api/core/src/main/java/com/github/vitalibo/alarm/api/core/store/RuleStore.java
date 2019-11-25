package com.github.vitalibo.alarm.api.core.store;

import com.github.vitalibo.alarm.api.core.model.Rule;

import java.util.concurrent.Future;

public interface RuleStore {

    Future<String> createRule(Rule rule);

    Future<Rule> updateRuleById(String ruleId, Rule rule);

    Future<Void> deleteRuleById(String ruleId);

    Future<Rule> getRuleById(String ruleId);

}