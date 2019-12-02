package com.github.vitalibo.alarm.processor.core.store;

import com.github.vitalibo.alarm.processor.core.model.Alarm;

import java.io.Serializable;
import java.util.List;

public interface AlarmStore extends Serializable {

    void update(String ruleId, Alarm.State state);

    void remove(String ruleId);

    List<Alarm> getAll();

}