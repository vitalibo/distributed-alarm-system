package com.github.vitalibo.alarm.api.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse<T> {

    private int statusCode;
    private Map<String, String> headers;
    private T body;

    public HttpResponse(T body) {
        this(200, body);
    }

    public HttpResponse(Integer statusCode, T body) {
        this(statusCode, new HashMap<>(), body);
    }

    public HttpResponse<T> withStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse<T> withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpResponse<T> withBody(T body) {
        this.body = body;
        return this;
    }

}
