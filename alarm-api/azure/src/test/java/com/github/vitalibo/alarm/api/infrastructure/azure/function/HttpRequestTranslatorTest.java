package com.github.vitalibo.alarm.api.infrastructure.azure.function;

import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

public class HttpRequestTranslatorTest {

    @Mock
    private HttpRequestMessage<String> mockHttpRequestMessage;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFrom() throws URISyntaxException {
        Mockito.when(mockHttpRequestMessage.getUri()).thenReturn(new URI("http://example.com"));
        Mockito.when(mockHttpRequestMessage.getHttpMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(mockHttpRequestMessage.getHeaders()).thenReturn(Collections.singletonMap("foo", "bar"));
        Mockito.when(mockHttpRequestMessage.getQueryParameters()).thenReturn(Collections.singletonMap("baz", "taz"));
        Mockito.when(mockHttpRequestMessage.getBody()).thenReturn("{}");

        HttpRequest actual = HttpRequestTranslator.from(mockHttpRequestMessage);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getPath(), "http://example.com");
        Assert.assertEquals(actual.getHttpMethod(), "GET");
        Assert.assertEquals(actual.getHeaders(), Collections.singletonMap("foo", "bar"));
        Assert.assertEquals(actual.getQueryStringParameters(), Collections.singletonMap("baz", "taz"));
        Assert.assertEquals(actual.getBody(), "{}");
    }

}