package com.github.vitalibo.alarm.api.infrastructure.aws.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.vitalibo.alarm.api.core.model.HttpResponse;
import com.github.vitalibo.alarm.api.core.util.Jackson;

final class ApiGatewayResponseTranslator {

    private ApiGatewayResponseTranslator() {
    }

    static APIGatewayProxyResponseEvent from(HttpResponse<?> response) {
        return new APIGatewayProxyResponseEvent()
            .withBody(Jackson.toJsonString(response.getBody()))
            .withStatusCode(response.getStatusCode())
            .withHeaders(response.getHeaders());
    }

}