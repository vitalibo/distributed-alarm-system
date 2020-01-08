package com.github.vitalibo.alarm.api.infrastructure.azure.function;

import com.github.vitalibo.alarm.api.core.model.HttpResponse;
import com.github.vitalibo.alarm.api.infrastructure.azure.function.HttpResponseMessageMock.HttpResponseMessageBuilderMock;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseTranslatorTest {

    @Mock
    private HttpRequestMessage<String> mockHttpRequestMessage;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testTranslate() {
        HttpResponseMessageBuilderMock dummyResponseBuilder = new HttpResponseMessageBuilderMock();
        Mockito.when(mockHttpRequestMessage.createResponseBuilder(Mockito.any()))
            .thenReturn(dummyResponseBuilder);
        HttpResponse<Map<String, String>> response = new HttpResponse<>();
        response.setStatusCode(200);
        Map<String, String> headers = new HashMap<>();
        headers.put("header1", "foo");
        headers.put("header2", "bar");
        response.setHeaders(headers);
        Map<String, String> body = new HashMap<>();
        body.put("foo", "bar");
        response.setBody(body);

        HttpResponseMessage actual = HttpResponseTranslator.from(mockHttpRequestMessage, response);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), 200);
        Assert.assertEquals(actual.getStatus(), HttpStatus.OK);
        Assert.assertEquals(actual.getHeader("header1"), "foo");
        Assert.assertEquals(actual.getHeader("header2"), "bar");
        Assert.assertEquals(actual.getBody(), "{\"foo\":\"bar\"}");
    }

}