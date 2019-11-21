package com.github.vitalibo.alarm.subject.core.producer;

import com.github.vitalibo.alarm.subject.core.Producer;

import java.io.PrintStream;

public class StandardOutputProducer implements Producer {

    private final PrintStream stream;

    public StandardOutputProducer() {
        this(System.out);
    }

    StandardOutputProducer(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void send(String value) {
        stream.println(value);
    }

}