package com.github.vitalibo.alarm.api.core.model.transform;

import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import com.github.vitalibo.alarm.api.core.model.UpdateRuleRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

public class UpdateRuleRequestTranslatorTest {

    @Test
    public void testFrom() {
        HttpRequest request = new HttpRequest();
        request.setQueryStringParameters(Collections.singletonMap("ruleId", "foo"));
        request.setBody("{\"metricName\":\"bar\", \"condition\":\"GreaterThanThreshold\", \"threshold\": 1.23}");

        UpdateRuleRequest actual = UpdateRuleRequestTranslator.from(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getRuleId(), "foo");
        Assert.assertEquals(actual.getMetricName(), "bar");
        Assert.assertEquals(actual.getCondition(), RuleCondition.GreaterThanThreshold);
        Assert.assertEquals(actual.getThreshold(), 1.23);
    }

}