package com.github.vitalibo.alarm.api.infrastructure.aws.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;

final class ApiGatewayRequestTranslator {

    private ApiGatewayRequestTranslator() {
    }

    static HttpRequest from(APIGatewayProxyRequestEvent event) {
        return new HttpRequest()
            .withPath(event.getPath())
            .withHttpMethod(event.getHttpMethod())
            .withHeaders(event.getHeaders())
            .withQueryStringParameters(event.getQueryStringParameters())
            .withPathParameters(event.getPathParameters())
            .withBody(event.getBody());
    }

}