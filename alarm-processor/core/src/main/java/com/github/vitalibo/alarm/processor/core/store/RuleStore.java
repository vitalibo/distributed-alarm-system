package com.github.vitalibo.alarm.processor.core.store;

import com.github.vitalibo.alarm.processor.core.model.Rule;

import java.io.Serializable;
import java.util.List;

public interface RuleStore extends Serializable {

    List<Rule> getAll();

}