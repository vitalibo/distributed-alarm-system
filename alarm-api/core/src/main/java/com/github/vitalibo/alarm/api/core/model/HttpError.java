package com.github.vitalibo.alarm.api.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
@JsonInclude(value = Include.NON_NULL)
public class HttpError {

    @JsonProperty(value = "status")
    private Integer status;

    @JsonProperty(value = "message")
    private String message;

    @JsonProperty(value = "errors")
    private Map<String, Collection<String>> errors;

    @JsonProperty(value = "request-id")
    private String requestId;

    public HttpError withStatus(Integer status) {
        this.status = status;
        return this;
    }

    public HttpError withMessage(String message) {
        this.message = message;
        return this;
    }

    public HttpError withErrors(Map<String, Collection<String>> errors) {
        this.errors = errors;
        return this;
    }

    public HttpError withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public HttpResponse<HttpError> build() {
        if (status == null) {
            throw new IllegalArgumentException("Status code can't be null.");
        }

        return new HttpResponse<HttpError>()
            .withStatusCode(status)
            .withBody(this);
    }

}