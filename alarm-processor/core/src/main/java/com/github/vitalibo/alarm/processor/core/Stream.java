package com.github.vitalibo.alarm.processor.core;

@FunctionalInterface
public interface Stream {

    void process(Spark spark);

}