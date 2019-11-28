package com.github.vitalibo.alarm.api.infrastructure.aws.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

public class ApiGatewayRequestTranslatorTest {

    @Test
    public void testFrom() {
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setPath("/foo");
        event.setHttpMethod("GET");
        event.setHeaders(Collections.singletonMap("header1", "foo"));
        event.setQueryStringParameters(Collections.singletonMap("param1", "bar"));
        event.setPathParameters(Collections.singletonMap("param2", "baz"));
        event.setBody("{}");

        HttpRequest actual = ApiGatewayRequestTranslator.from(event);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getPath(), "/foo");
        Assert.assertEquals(actual.getHttpMethod(), "GET");
        Assert.assertEquals(actual.getHeaders(), Collections.singletonMap("header1", "foo"));
        Assert.assertEquals(actual.getQueryStringParameters(), Collections.singletonMap("param1", "bar"));
        Assert.assertEquals(actual.getPathParameters(), Collections.singletonMap("param2", "baz"));
        Assert.assertEquals(actual.getBody(), "{}");
    }

}