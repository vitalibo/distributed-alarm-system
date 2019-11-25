package com.github.vitalibo.alarm.api.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public final class Resources {

    private Resources() {
    }

    public static String asString(String resource) {
        return Resources.asBufferedReader(resource)
            .lines()
            .collect(Collectors.joining(System.lineSeparator()));
    }

    public static BufferedReader asBufferedReader(String resource) {
        return new BufferedReader(
            new InputStreamReader(
                Resources.asInputStream(resource)));
    }

    public static InputStream asInputStream(String resource) {
        return Resources.class.getResourceAsStream(resource);
    }

}