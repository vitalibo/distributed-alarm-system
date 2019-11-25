package com.github.vitalibo.alarm.api.core.model;

import java.io.Serializable;

public enum RuleCondition implements Serializable {

    GreaterThanOrEqualToThreshold,
    GreaterThanThreshold,
    LessThanOrEqualToThreshold,
    LessThanThreshold

}