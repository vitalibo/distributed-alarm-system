package com.github.vitalibo.alarm.processor.core.model;

import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class Metric implements Serializable {

    private String name;
    private OffsetDateTime timestamp;
    private Double value;

    public Metric withName(String name) {
        this.name = name;
        return this;
    }

    public Metric withTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Metric withValue(Double value) {
        this.value = value;
        return this;
    }

}