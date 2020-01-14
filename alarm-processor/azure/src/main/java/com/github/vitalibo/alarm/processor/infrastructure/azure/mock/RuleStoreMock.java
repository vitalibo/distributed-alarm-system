package com.github.vitalibo.alarm.processor.infrastructure.azure.mock;

import com.github.vitalibo.alarm.processor.core.model.Rule;
import com.github.vitalibo.alarm.processor.core.store.RuleStore;

import java.util.Collections;
import java.util.List;

public class RuleStoreMock implements RuleStore {
    @Override
    public List<Rule> getAll() {
        return Collections.emptyList();
    }
}
