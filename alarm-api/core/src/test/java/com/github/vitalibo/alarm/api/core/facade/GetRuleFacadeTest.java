package com.github.vitalibo.alarm.api.core.facade;

import com.github.vitalibo.alarm.api.core.model.*;
import com.github.vitalibo.alarm.api.core.store.AlarmStore;
import com.github.vitalibo.alarm.api.core.store.RuleStore;
import com.github.vitalibo.alarm.api.core.util.ErrorState;
import com.github.vitalibo.alarm.api.core.util.ValidationException;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.concurrent.Future;

public class GetRuleFacadeTest {

    @Mock
    private RuleStore mockRuleStore;
    @Mock
    private AlarmStore mockAlarmStore;
    @Mock
    private Future<Rule> mockFutureRule;
    @Mock
    private Future<Status> mockFutureStatus;
    @Captor
    private ArgumentCaptor<GetRuleRequest> captorGetRuleRequest;

    private GetRuleFacade spyGetRuleFacade;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spyGetRuleFacade = Mockito.spy(new GetRuleFacade(mockAlarmStore, mockRuleStore));
    }

    @Test
    public void testProcessHttpRequest() {
        HttpRequest request = new HttpRequest();
        request.setQueryStringParameters(Collections.singletonMap("ruleId", "foo"));
        GetRuleResponse response = new GetRuleResponse();
        Mockito.doReturn(response).when(spyGetRuleFacade).process(Mockito.any(GetRuleRequest.class));

        HttpResponse<GetRuleResponse> actual = spyGetRuleFacade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), 200);
        Assert.assertEquals(actual.getBody(), response);
        Mockito.verify(spyGetRuleFacade).process(captorGetRuleRequest.capture());
        GetRuleRequest value = captorGetRuleRequest.getValue();
        Assert.assertEquals(value.getRuleId(), "foo");
    }

    @Test
    public void testFailProcessHttpRequest() {
        HttpRequest request = new HttpRequest();
        request.setQueryStringParameters(Collections.emptyMap());
        GetRuleResponse response = new GetRuleResponse();
        Mockito.doReturn(response).when(spyGetRuleFacade).process(Mockito.any(GetRuleRequest.class));

        ValidationException actual = Assert.expectThrows(ValidationException.class,
            () -> spyGetRuleFacade.process(request));

        ErrorState errorState = actual.getErrorState();
        Assert.assertNotNull(errorState.get("ruleId"));
    }

    @Test
    public void testProcess() throws Exception {
        Mockito.when(mockAlarmStore.getStatusByRuleId(Mockito.anyString())).thenReturn(mockFutureStatus);
        Mockito.when(mockRuleStore.getRuleById(Mockito.anyString())).thenReturn(mockFutureRule);
        Rule rule = new Rule()
            .withMetricName("bar")
            .withCondition(RuleCondition.LessThanThreshold)
            .withThreshold(1.23);
        Mockito.when(mockFutureStatus.get()).thenReturn(Status.Alarm);
        Mockito.when(mockFutureRule.get()).thenReturn(rule);
        GetRuleRequest request = new GetRuleRequest()
            .withRuleId("foo");

        GetRuleResponse actual = spyGetRuleFacade.process(request);

        Assert.assertNotNull(actual);
        Mockito.verify(mockRuleStore).getRuleById("foo");
        Mockito.verify(mockAlarmStore).getStatusByRuleId("foo");
        Assert.assertEquals(actual.getMetricName(), "bar");
        Assert.assertEquals(actual.getCondition(), RuleCondition.LessThanThreshold);
        Assert.assertEquals(actual.getThreshold(), 1.23);
        Assert.assertEquals(actual.getStatus(), Status.Alarm);
    }

}