package com.github.vitalibo.alarm.processor.core;

import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class TestHelper {

    private TestHelper() {
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T serDe(T object) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(bytes);

        outputStream.writeObject(object);
        outputStream.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toByteArray());

        return (T) new ObjectInputStream(inputStream)
            .readObject();
    }

}