package com.github.vitalibo.alarm.api.infrastructure.aws.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.vitalibo.alarm.api.core.model.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;

public class ApiGatewayResponseTranslatorTest {

    @Test
    public void testFrom() {
        HttpResponse<Map<String, ?>> response = new HttpResponse<>();
        response.setStatusCode(200);
        response.setHeaders(Collections.singletonMap("foo", "bar"));
        response.setBody(Collections.singletonMap("baz", 1));

        APIGatewayProxyResponseEvent actual = ApiGatewayResponseTranslator.from(response);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), (Integer) 200);
        Assert.assertEquals(actual.getHeaders(), Collections.singletonMap("foo", "bar"));
        Assert.assertEquals(actual.getBody(), "{\"baz\":1}");
    }

}