package com.github.vitalibo.alarm.api.core.model;

import lombok.Data;

import java.util.Map;

@Data
public class HttpRequest {

    private String path;
    private String httpMethod;
    private Map<String, String> headers;
    private Map<String, String> queryStringParameters;
    private Map<String, String> pathParameters;
    private String body;

    public HttpRequest withPath(String path) {
        this.path = path;
        return this;
    }

    public HttpRequest withHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public HttpRequest withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequest withQueryStringParameters(Map<String, String> queryStringParameters) {
        this.queryStringParameters = queryStringParameters;
        return this;
    }

    public HttpRequest withPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
        return this;
    }

    public HttpRequest withBody(String body) {
        this.body = body;
        return this;
    }

}