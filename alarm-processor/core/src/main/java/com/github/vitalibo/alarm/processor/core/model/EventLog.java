package com.github.vitalibo.alarm.processor.core.model;

import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class EventLog<T> implements Serializable {

    private String table;
    private OffsetDateTime timestamp;
    private Type type;
    private T payload;

    public EventLog<T> withTable(String table) {
        this.table = table;
        return this;
    }

    public EventLog<T> withTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public EventLog<T> withType(Type type) {
        this.type = type;
        return this;
    }

    public EventLog<T> withPayload(T payload) {
        this.payload = payload;
        return this;
    }

    public enum Type implements Serializable {

        Load,
        Insert,
        Update,
        Delete

    }

}