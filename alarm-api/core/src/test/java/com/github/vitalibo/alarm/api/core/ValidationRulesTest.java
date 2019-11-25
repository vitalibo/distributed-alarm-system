package com.github.vitalibo.alarm.api.core;

import com.github.vitalibo.alarm.api.core.model.CreateRuleRequest;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import com.github.vitalibo.alarm.api.core.util.ErrorState;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;

public class ValidationRulesTest {

    private ErrorState errorState;

    @BeforeMethod
    public void setUp() {
        errorState = new ErrorState();
    }

    @DataProvider
    public Object[][] samplesBody() {
        return new Object[][]{
            {"{}"}, {"{\"metricName\": \"foo\"}"}
        };
    }

    @Test(dataProvider = "samplesBody")
    public void testVerifyJsonBody(String body) {
        HttpRequest request = new HttpRequest();
        request.setBody(body);

        ValidationRules.verifyJsonBody(request, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @DataProvider
    public Object[][] samplesIncorrectBody() {
        return new Object[][]{
            {null}, {""}, {"{\"foo\": \"bar\"}"}
        };
    }

    @Test(dataProvider = "samplesIncorrectBody")
    public void testVerifyFailJsonBody(String body) {
        HttpRequest request = new HttpRequest();
        request.setBody(body);

        ValidationRules.verifyJsonBody(request, errorState);

        Assert.assertTrue(errorState.hasErrors());
    }

    @Test
    public void testVerifyRuleId() {
        HttpRequest request = new HttpRequest();
        request.setQueryStringParameters(Collections.singletonMap("ruleId", "foo"));

        ValidationRules.verifyRuleId(request, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @Test
    public void testVerifyFailRuleId() {
        HttpRequest request = new HttpRequest();
        request.setQueryStringParameters(Collections.emptyMap());

        ValidationRules.verifyRuleId(request, errorState);

        Assert.assertTrue(errorState.hasErrors());
    }

    @Test
    public void testVerifyMetricName() {
        CreateRuleRequest request = new CreateRuleRequest()
            .withMetricName("foo");

        ValidationRules.verifyMetricName(request, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @Test
    public void testVerifyFailMetricName() {
        CreateRuleRequest request = new CreateRuleRequest();

        ValidationRules.verifyMetricName(request, errorState);

        Assert.assertTrue(errorState.hasErrors());
    }

    @Test
    public void testVerifyCondition() {
        CreateRuleRequest request = new CreateRuleRequest()
            .withCondition(RuleCondition.LessThanOrEqualToThreshold);

        ValidationRules.verifyCondition(request, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @Test
    public void testVerifyFailCondition() {
        CreateRuleRequest request = new CreateRuleRequest();

        ValidationRules.verifyCondition(request, errorState);

        Assert.assertTrue(errorState.hasErrors());
    }

    @Test
    public void testVerifyThreshold() {
        CreateRuleRequest request = new CreateRuleRequest()
            .withThreshold(1.234);

        ValidationRules.verifyThreshold(request, errorState);

        Assert.assertFalse(errorState.hasErrors());
    }

    @Test
    public void testVerifyFailThreshold() {
        CreateRuleRequest request = new CreateRuleRequest();

        ValidationRules.verifyThreshold(request, errorState);

        Assert.assertTrue(errorState.hasErrors());
    }

}