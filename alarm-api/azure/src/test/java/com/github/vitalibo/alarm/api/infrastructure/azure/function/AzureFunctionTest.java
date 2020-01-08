package com.github.vitalibo.alarm.api.infrastructure.azure.function;

import com.github.vitalibo.alarm.api.core.Facade;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.HttpResponse;
import com.github.vitalibo.alarm.api.core.util.ErrorState;
import com.github.vitalibo.alarm.api.core.util.ValidationException;
import com.github.vitalibo.alarm.api.infrastructure.azure.Factory;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;


public class AzureFunctionTest {

    @Mock
    private Factory mockFactory;
    @Mock
    private HttpRequestMessage<String> mockHttpRequestMessage;
    @Mock
    private ExecutionContext mockExecutionContext;
    @Captor
    private ArgumentCaptor<HttpRequest> captorHttpRequest;
    @Mock
    private Facade mockFacade;
    @Mock
    private Logger mockLogger;

    private HttpFunction spyHttpFunction;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spyHttpFunction = Mockito.spy(new HttpFunction(mockFactory));
        Mockito.when(mockFactory.createCreateRuleFacade()).thenReturn(mockFacade);
        Mockito.when(mockFactory.createDeleteRuleFacade()).thenReturn(mockFacade);
        Mockito.when(mockFactory.createGetRuleFacade()).thenReturn(mockFacade);
        Mockito.when(mockFactory.createUpdateRuleFacade()).thenReturn(mockFacade);
        Mockito.when(mockExecutionContext.getLogger()).thenReturn(mockLogger);
    }

    @Test
    public void testHandleRequest() {
        Mockito.when(mockHttpRequestMessage.createResponseBuilder(Mockito.any()))
            .thenReturn(new HttpResponseMessageMock.HttpResponseMessageBuilderMock());
        HttpResponse<Map<String, String>> response = new HttpResponse<Map<String, String>>()
            .withStatusCode(200)
            .withHeaders(Collections.emptyMap())
            .withBody(Collections.singletonMap("foo", "bar"));
        Mockito.when(mockHttpRequestMessage.getBody()).thenReturn("taz");
        Mockito.doReturn(response)
            .when(spyHttpFunction).process(Mockito.any(), Mockito.any());

        HttpResponseMessage actual = spyHttpFunction.handleRequest(mockHttpRequestMessage, mockExecutionContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), 200);
        Assert.assertEquals(actual.getBody(), "{\"foo\":\"bar\"}");
        Mockito.verify(spyHttpFunction).process(captorHttpRequest.capture(), Mockito.eq(mockExecutionContext));
        HttpRequest value = captorHttpRequest.getValue();
        Assert.assertEquals(value.getBody(), "taz");
    }

    @Test
    public void testHandleRequestRouteDeleteRule() {
        HttpRequest request = new HttpRequest();
        request.setHttpMethod("DELETE");
        HttpResponse<String> response = new HttpResponse<>(200, "foo");
        Mockito.when(mockFacade.process(request)).thenReturn(response);

        HttpResponse<?> actual = spyHttpFunction.process(request, mockExecutionContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, response);
        Mockito.verify(mockFactory, Mockito.never()).createCreateRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createGetRuleFacade();
        Mockito.verify(mockFactory).createDeleteRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createUpdateRuleFacade();
        Mockito.verify(mockFacade).process(request);
    }

    @Test
    public void testHandleRequestRouteNotFound() {
        HttpRequest request = new HttpRequest();
        request.setHttpMethod("HEAD");

        HttpResponse<?> actual = spyHttpFunction.process(request, mockExecutionContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), 404);
        Assert.assertTrue(String.valueOf(actual.getBody()).contains("Not Found"));
        Mockito.verify(mockFactory, Mockito.never()).createCreateRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createGetRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createDeleteRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createUpdateRuleFacade();
        Mockito.verify(mockFacade, Mockito.never()).process(request);
    }

    @Test
    public void testHandleRequestRouteCreateRule() {
        HttpRequest request = new HttpRequest();
        request.setPath("/rules");
        request.setHttpMethod("PUT");
        HttpResponse<String> response = new HttpResponse<>(200, "foo");
        Mockito.when(mockFacade.process(request)).thenReturn(response);

        HttpResponse<?> actual = spyHttpFunction.process(request, mockExecutionContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, response);
        Mockito.verify(mockFactory).createCreateRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createGetRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createDeleteRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createUpdateRuleFacade();
        Mockito.verify(mockFacade).process(request);
    }

    @Test
    public void testHandleRequestRouteUpdateRule() {
        HttpRequest request = new HttpRequest();
        request.setHttpMethod("POST");
        HttpResponse<String> response = new HttpResponse<>(200, "foo");
        Mockito.when(mockFacade.process(request)).thenReturn(response);

        HttpResponse<?> actual = spyHttpFunction.process(request, mockExecutionContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, response);
        Mockito.verify(mockFactory, Mockito.never()).createCreateRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createGetRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createDeleteRuleFacade();
        Mockito.verify(mockFactory).createUpdateRuleFacade();
        Mockito.verify(mockFacade).process(request);
    }

    @Test
    public void testHandleRequestRouteGetRule() {
        HttpRequest request = new HttpRequest();
        request.setHttpMethod("GET");
        HttpResponse<String> response = new HttpResponse<>(200, "foo");
        Mockito.when(mockFacade.process(request)).thenReturn(response);

        HttpResponse<?> actual = spyHttpFunction.process(request, mockExecutionContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, response);
        Mockito.verify(mockFactory, Mockito.never()).createCreateRuleFacade();
        Mockito.verify(mockFactory).createGetRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createDeleteRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createUpdateRuleFacade();
        Mockito.verify(mockFacade).process(request);
    }

    @Test
    public void testHandleRequestBadRequest() {
        HttpRequest request = new HttpRequest();
        request.setHttpMethod("PUT");
        ErrorState errorState = new ErrorState();
        errorState.addError("foo", "bar");
        Mockito.when(mockFacade.process(request)).thenThrow(new ValidationException(errorState));

        HttpResponse<?> actual = spyHttpFunction.process(request, mockExecutionContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), 400);
        Assert.assertTrue(String.valueOf(actual.getBody()).contains("Bad Request"));
        Assert.assertTrue(String.valueOf(actual.getBody()).matches(".*foo.*bar.*"));
        Mockito.verify(mockFactory).createCreateRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createGetRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createDeleteRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createUpdateRuleFacade();
        Mockito.verify(mockFacade).process(request);
    }

    @Test
    public void testHandleRequestInternalServerError() {
        HttpRequest request = new HttpRequest();
        request.setHttpMethod("PUT");
        Mockito.when(mockFacade.process(request)).thenThrow(IllegalArgumentException.class);

        HttpResponse<?> actual = spyHttpFunction.process(request, mockExecutionContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), 500);
        Assert.assertTrue(String.valueOf(actual.getBody()).contains("Internal Server Error"));
        Mockito.verify(mockFactory).createCreateRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createGetRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createDeleteRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createUpdateRuleFacade();
        Mockito.verify(mockFacade).process(request);
    }

}