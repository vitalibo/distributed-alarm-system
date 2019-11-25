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

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class UpdateRuleFacadeTest {

    @Mock
    private RuleStore mockRuleStore;
    @Captor
    private ArgumentCaptor<UpdateRuleRequest> captorUpdateRuleRequest;
    @Mock
    private Future<Rule> mockFuture;
    @Captor
    private ArgumentCaptor<Rule> captorRule;

    private UpdateRuleFacade spyUpdateRuleFacade;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spyUpdateRuleFacade = Mockito.spy(new UpdateRuleFacade(mockRuleStore));
    }

    @Test
    public void testProcessHttpRequest() {
        HttpRequest request = new HttpRequest();
        request.setQueryStringParameters(Collections.singletonMap("ruleId", "foo"));
        request.setBody(Resources.asString("/UpdateRuleRequest.json"));
        UpdateRuleResponse response = new UpdateRuleResponse();
        response.withMetricName("bar");
        Mockito.doReturn(response).when(spyUpdateRuleFacade).process(Mockito.any(UpdateRuleRequest.class));

        HttpResponse<UpdateRuleResponse> actual = spyUpdateRuleFacade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), 200);
        Assert.assertEquals(actual.getBody(), response);
        Mockito.verify(spyUpdateRuleFacade).process(captorUpdateRuleRequest.capture());
        UpdateRuleRequest value = captorUpdateRuleRequest.getValue();
        Assert.assertEquals(value.getRuleId(), "foo");
        Assert.assertNull(value.getMetricName());
        Assert.assertEquals(value.getCondition(), RuleCondition.GreaterThanOrEqualToThreshold);
        Assert.assertEquals(value.getThreshold(), 1.234);
    }

    @Test
    public void testFailProcessHttpRequest() {
        HttpRequest request = new HttpRequest();
        request.setQueryStringParameters(Collections.emptyMap());
        UpdateRuleResponse response = new UpdateRuleResponse();
        response.setMetricName("foo");
        Mockito.doReturn(response).when(spyUpdateRuleFacade).process(Mockito.any(UpdateRuleRequest.class));

        ValidationException actual = Assert.expectThrows(ValidationException.class,
            () -> spyUpdateRuleFacade.process(request));

        ErrorState errorState = actual.getErrorState();
        Assert.assertNotNull(errorState.get("body"));
    }

    @Test
    public void testProcess() throws ExecutionException, InterruptedException {
        Mockito.when(mockRuleStore.updateRuleById(Mockito.anyString(), Mockito.any())).thenReturn(mockFuture);
        Rule rule = new Rule()
            .withMetricName("metric")
            .withCondition(RuleCondition.LessThanOrEqualToThreshold)
            .withThreshold(1.234);
        Mockito.when(mockFuture.get()).thenReturn(rule);
        UpdateRuleRequest request = new UpdateRuleRequest()
            .withRuleId("foo")
            .withCondition(RuleCondition.LessThanThreshold);

        UpdateRuleResponse actual = spyUpdateRuleFacade.process(request);

        Assert.assertNotNull(actual);
        Mockito.verify(mockRuleStore).updateRuleById(Mockito.eq("foo"), captorRule.capture());
        Rule value = captorRule.getValue();
        Assert.assertEquals(value.getCondition(), RuleCondition.LessThanThreshold);
        Assert.assertEquals(actual.getMetricName(), "metric");
        Assert.assertEquals(actual.getCondition(), RuleCondition.LessThanOrEqualToThreshold);
        Assert.assertEquals(actual.getThreshold(), 1.234);
    }

}