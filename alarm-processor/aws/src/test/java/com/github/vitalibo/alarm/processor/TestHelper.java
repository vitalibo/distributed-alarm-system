package com.github.vitalibo.alarm.processor;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

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

    @RequiredArgsConstructor
    public static class Reflection<T> {
        private final Class<T> cls;
        private final T actual;

        @SneakyThrows
        @SuppressWarnings("unchecked")
        public <K> K field(String fieldName) {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (K) field.get(actual);
        }
    }

}