package com.github.vitalibo.alarm.api.core.store;

import com.github.vitalibo.alarm.api.core.model.Status;

import java.util.concurrent.Future;

public interface AlarmStore {

    Future<Status> getStatusByRuleId(String ruleId);

}