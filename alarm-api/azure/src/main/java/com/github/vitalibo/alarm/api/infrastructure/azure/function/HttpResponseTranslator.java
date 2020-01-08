package com.github.vitalibo.alarm.api.infrastructure.azure.function;

import com.github.vitalibo.alarm.api.core.model.HttpResponse;
import com.github.vitalibo.alarm.api.core.util.Jackson;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

import java.util.Map;

class HttpResponseTranslator {

    private HttpResponseTranslator() {
    }

    static HttpResponseMessage from(HttpRequestMessage<String> request, HttpResponse<?> response) {
        HttpResponseMessage.Builder builder = request.createResponseBuilder(HttpStatus.valueOf(response.getStatusCode()));
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            builder = builder.header(entry.getKey(), entry.getValue());
        }

        return builder
            .status(HttpStatus.valueOf(response.getStatusCode()))
            .header("Content-Type", "application/json; charset=utf-8")
            .body(Jackson.toJsonString(response.getBody()))
            .build();
    }

}