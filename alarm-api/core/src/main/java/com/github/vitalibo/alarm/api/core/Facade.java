package com.github.vitalibo.alarm.api.core;

import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.HttpResponse;

@FunctionalInterface
public interface Facade<Request, Response> {

    default HttpResponse<Response> process(HttpRequest request) {
        throw new IllegalStateException("Method not implemented.");
    }

    Response process(Request request);

}