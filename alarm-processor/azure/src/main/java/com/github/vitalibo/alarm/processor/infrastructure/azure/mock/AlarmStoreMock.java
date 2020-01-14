package com.github.vitalibo.alarm.processor.infrastructure.azure.mock;

import com.github.vitalibo.alarm.processor.core.model.Alarm;
import com.github.vitalibo.alarm.processor.core.store.AlarmStore;

import java.util.Collections;
import java.util.List;

public class AlarmStoreMock implements AlarmStore {
    @Override
    public void update(String ruleId, Alarm.State state) {

    }

    @Override
    public void remove(String ruleId) {

    }

    @Override
    public List<Alarm> getAll() {
        return Collections.emptyList();
    }
}