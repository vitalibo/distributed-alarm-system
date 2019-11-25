package com.github.vitalibo.alarm.api.core.facade;

import com.github.vitalibo.alarm.api.core.model.DeleteRuleRequest;
import com.github.vitalibo.alarm.api.core.model.DeleteRuleResponse;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.HttpResponse;
import com.github.vitalibo.alarm.api.core.store.RuleStore;
import com.github.vitalibo.alarm.api.core.util.ErrorState;
import com.github.vitalibo.alarm.api.core.util.ValidationException;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

public class DeleteRuleFacadeTest {

    @Mock
    private RuleStore mockRuleStore;
    @Captor
    private ArgumentCaptor<DeleteRuleRequest> captorDeleteRuleRequest;

    private DeleteRuleFacade spyDeleteRuleFacade;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spyDeleteRuleFacade = Mockito.spy(new DeleteRuleFacade(mockRuleStore));
    }

    @Test
    public void testProcessHttpRequest() {
        HttpRequest request = new HttpRequest();
        request.setQueryStringParameters(Collections.singletonMap("ruleId", "foo"));
        DeleteRuleResponse response = new DeleteRuleResponse();
        Mockito.doReturn(response).when(spyDeleteRuleFacade).process(Mockito.any(DeleteRuleRequest.class));

        HttpResponse<DeleteRuleResponse> actual = spyDeleteRuleFacade.process(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getStatusCode(), 200);
        Assert.assertEquals(actual.getBody(), response);
        Mockito.verify(spyDeleteRuleFacade).process(captorDeleteRuleRequest.capture());
        DeleteRuleRequest value = captorDeleteRuleRequest.getValue();
        Assert.assertEquals(value.getRuleId(), "foo");
    }

    @Test
    public void testFailProcessHttpRequest() {
        HttpRequest request = new HttpRequest();
        request.setQueryStringParameters(Collections.emptyMap());
        DeleteRuleResponse response = new DeleteRuleResponse();
        Mockito.doReturn(response).when(spyDeleteRuleFacade).process(Mockito.any(DeleteRuleRequest.class));

        ValidationException actual = Assert.expectThrows(ValidationException.class,
            () -> spyDeleteRuleFacade.process(request));

        ErrorState errorState = actual.getErrorState();
        Assert.assertNotNull(errorState.get("ruleId"));
    }

    @Test
    public void testProcess() {
        DeleteRuleRequest request = new DeleteRuleRequest()
            .withRuleId("foo");

        DeleteRuleResponse actual = spyDeleteRuleFacade.process(request);

        Assert.assertNotNull(actual);
        Mockito.verify(mockRuleStore).deleteRuleById("foo");
    }

}