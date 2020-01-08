package com.github.vitalibo.alarm.api.infrastructure.azure.function;

import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.microsoft.azure.functions.HttpRequestMessage;

import java.util.Collections;

class HttpRequestTranslator {

    private HttpRequestTranslator() {
    }

    static HttpRequest from(HttpRequestMessage<String> request) {
        return new HttpRequest()
            .withPath(String.valueOf(request.getUri()))
            .withHttpMethod(String.valueOf(request.getHttpMethod()))
            .withHeaders(request.getHeaders())
            .withQueryStringParameters(request.getQueryParameters())
            .withPathParameters(Collections.emptyMap())
            .withBody(request.getBody());
    }

}