package com.github.vitalibo.alarm.api.infrastructure.aws;

import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.regex.Pattern;

public enum Routes {

    CreateRule("/rules", "PUT"),
    DeleteRule("/rules", "DELETE"),
    GetRule("/rules", "GET"),
    UpdateRule("/rules", "POST"),

    NotFound(".*", ".*");

    @Getter
    private final Pattern path;
    @Getter
    private final Pattern httpMethod;

    Routes(String path, String httpMethod) {
        this.path = Pattern.compile(path);
        this.httpMethod = Pattern.compile(httpMethod);
    }

    public static Routes route(HttpRequest request) {
        return Arrays.stream(Routes.values())
            .filter(route -> route.getPath()
                .matcher(request.getPath())
                .matches())
            .filter(route -> route.getHttpMethod()
                .matcher(request.getHttpMethod())
                .matches())
            .findFirst()
            .orElse(Routes.NotFound);
    }

}