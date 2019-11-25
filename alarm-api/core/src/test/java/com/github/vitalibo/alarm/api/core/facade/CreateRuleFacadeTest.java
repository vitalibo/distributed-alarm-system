package com.github.vitalibo.alarm.api.core.facade;

import com.github.vitalibo.alarm.api.core.model.*;
import com.github.vitalibo.alarm.api.core.store.RuleStore;
import com.github.vitalibo.alarm.api.core.util.ErrorState;
import com.github.vitalibo.alarm.api.core.util.Resources;
import com.github.vitalibo.alarm.api.core.util.ValidationException;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CreateRuleFacadeTest {

    @Mock
    private RuleStore mockRuleStore;
    @Captor
    private ArgumentCaptor<CreateRuleRequest> captorCreateRuleRequest;
    @Mock
    private Future<String> mockFuture;
    @Captor
    private ArgumentCaptor<Rule> captorRule;

    private CreateRuleFacade spyCreateRuleFacade;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spyCreateRuleFacade = Mockito.spy(new CreateRuleFacade(mockRuleStore));
    }

    @Test
    public void testProcessHttpRequest() {
        HttpRequest request = new HttpRequest();
        request.setBody(Resources.asString("/CreateRuleRequest.json"));
        CreateRuleResponse response = new CreateRuleResponse();
        response.setRuleId("foo");
        Mockito.doReturn(response).when(spyCreateRuleFacade).process(Mockito.any(CreateRuleRequest.class));

        HttpResponse<CreateRuleResponse> actual = spyCreateRuleFacade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), 200);
        Assert.assertEquals(actual.getBody(), response);
        Mockito.verify(spyCreateRuleFacade).process(captorCreateRuleRequest.capture());
        CreateRuleRequest value = captorCreateRuleRequest.getValue();
        Assert.assertEquals(value.getMetricName(), "foo");
        Assert.assertEquals(value.getCondition(), RuleCondition.GreaterThanOrEqualToThreshold);
        Assert.assertEquals(value.getThreshold(), 1.234);
    }

    @Test
    public void testFailProcessHttpRequest() {
        CreateRuleResponse response = new CreateRuleResponse();
        response.setRuleId("foo");
        Mockito.doReturn(response).when(spyCreateRuleFacade).process(Mockito.any(CreateRuleRequest.class));

        ValidationException actual = Assert.expectThrows(ValidationException.class,
            () -> spyCreateRuleFacade.process(new HttpRequest()));

        ErrorState errorState = actual.getErrorState();
        Assert.assertNotNull(errorState.get("body"));
    }

    @Test
    public void testProcess() throws ExecutionException, InterruptedException {
        Mockito.when(mockRuleStore.createRule(Mockito.any())).thenReturn(mockFuture);
        Mockito.when(mockFuture.get()).thenReturn("bar");
        CreateRuleRequest request = new CreateRuleRequest()
            .withMetricName("foo")
            .withCondition(RuleCondition.GreaterThanThreshold)
            .withThreshold(1.234);

        CreateRuleResponse actual = spyCreateRuleFacade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getRuleId(), "bar");
        Mockito.verify(mockRuleStore).createRule(captorRule.capture());
        Rule value = captorRule.getValue();
        Assert.assertEquals(value.getMetricName(), "foo");
        Assert.assertEquals(value.getCondition(), RuleCondition.GreaterThanThreshold);
        Assert.assertEquals(value.getThreshold(), 1.234);
    }

    @Test
    public void testFailProcess() {
        ValidationException actual = Assert.expectThrows(ValidationException.class,
            () -> spyCreateRuleFacade.process(new CreateRuleRequest()));

        ErrorState errorState = actual.getErrorState();
        Assert.assertEquals(errorState.get("metricName"), Arrays.asList("Required fields cannot be empty."));
        Assert.assertEquals(errorState.get("condition"), Arrays.asList("Required fields cannot be empty."));
        Assert.assertEquals(errorState.get("threshold"), Arrays.asList("Required fields cannot be empty."));
    }

}