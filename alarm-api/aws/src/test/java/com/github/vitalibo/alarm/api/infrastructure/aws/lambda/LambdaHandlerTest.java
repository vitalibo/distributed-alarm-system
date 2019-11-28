package com.github.vitalibo.alarm.api.infrastructure.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.vitalibo.alarm.api.core.Facade;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.HttpResponse;
import com.github.vitalibo.alarm.api.core.util.ErrorState;
import com.github.vitalibo.alarm.api.core.util.ValidationException;
import com.github.vitalibo.alarm.api.infrastructure.aws.Factory;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

public class LambdaHandlerTest {

    @Mock
    private Factory mockFactory;
    @Mock
    private Context mockContext;
    @Mock
    private Facade mockFacade;
    @Captor
    private ArgumentCaptor<HttpRequest> captorHttpRequest;

    private LambdaHandler spyLambdaHandler;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spyLambdaHandler = Mockito.spy(new LambdaHandler(mockFactory));
        Mockito.when(mockFactory.createCreateRuleFacade()).thenReturn(mockFacade);
        Mockito.when(mockFactory.createDeleteRuleFacade()).thenReturn(mockFacade);
        Mockito.when(mockFactory.createGetRuleFacade()).thenReturn(mockFacade);
        Mockito.when(mockFactory.createUpdateRuleFacade()).thenReturn(mockFacade);
    }

    @Test
    public void testHandleRequest() {
        Mockito.doReturn(new HttpResponse<>(200, Collections.singletonMap("foo", "bar")))
            .when(spyLambdaHandler).handleRequest(Mockito.any(HttpRequest.class), Mockito.eq(mockContext));
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("requestPath");

        APIGatewayProxyResponseEvent actual = spyLambdaHandler.handleRequest(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), (Integer) 200);
        Assert.assertEquals(actual.getBody(), "{\"foo\":\"bar\"}");
        Mockito.verify(spyLambdaHandler).handleRequest(captorHttpRequest.capture(), Mockito.eq(mockContext));
        HttpRequest value = captorHttpRequest.getValue();
        Assert.assertEquals(value.getPath(), "requestPath");
    }

    @Test
    public void testHandleRequestRouteCreateRule() {
        HttpRequest request = new HttpRequest();
        request.setPath("/rules");
        request.setHttpMethod("PUT");
        HttpResponse<String> response = new HttpResponse<>(200, "foo");
        Mockito.when(mockFacade.process(request)).thenReturn(response);

        HttpResponse<?> actual = spyLambdaHandler.handleRequest(request, mockContext);

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
        request.setPath("/rules");
        request.setHttpMethod("POST");
        HttpResponse<String> response = new HttpResponse<>(200, "foo");
        Mockito.when(mockFacade.process(request)).thenReturn(response);

        HttpResponse<?> actual = spyLambdaHandler.handleRequest(request, mockContext);

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
        request.setPath("/rules");
        request.setHttpMethod("GET");
        HttpResponse<String> response = new HttpResponse<>(200, "foo");
        Mockito.when(mockFacade.process(request)).thenReturn(response);

        HttpResponse<?> actual = spyLambdaHandler.handleRequest(request, mockContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, response);
        Mockito.verify(mockFactory, Mockito.never()).createCreateRuleFacade();
        Mockito.verify(mockFactory).createGetRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createDeleteRuleFacade();
        Mockito.verify(mockFactory, Mockito.never()).createUpdateRuleFacade();
        Mockito.verify(mockFacade).process(request);
    }

    @Test
    public void testHandleRequestRouteDeleteRule() {
        HttpRequest request = new HttpRequest();
        request.setPath("/rules");
        request.setHttpMethod("DELETE");
        HttpResponse<String> response = new HttpResponse<>(200, "foo");
        Mockito.when(mockFacade.process(request)).thenReturn(response);

        HttpResponse<?> actual = spyLambdaHandler.handleRequest(request, mockContext);

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
        request.setPath("/foo");
        request.setHttpMethod("GET");

        HttpResponse<?> actual = spyLambdaHandler.handleRequest(request, mockContext);

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
    public void testHandleRequestBadRequest() {
        HttpRequest request = new HttpRequest();
        request.setPath("/rules");
        request.setHttpMethod("PUT");
        ErrorState errorState = new ErrorState();
        errorState.addError("foo", "bar");
        Mockito.when(mockFacade.process(request)).thenThrow(new ValidationException(errorState));

        HttpResponse<?> actual = spyLambdaHandler.handleRequest(request, mockContext);

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
        request.setPath("/rules");
        request.setHttpMethod("PUT");
        Mockito.when(mockFacade.process(request)).thenThrow(IllegalArgumentException.class);

        HttpResponse<?> actual = spyLambdaHandler.handleRequest(request, mockContext);

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