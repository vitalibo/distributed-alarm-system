package com.github.vitalibo.alarm.api.core.model.transform;

import com.github.vitalibo.alarm.api.core.model.CreateRuleRequest;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CreateRuleRequestTranslatorTest {

    @Test
    public void testFrom() {
        HttpRequest request = new HttpRequest();
        request.setBody("{\"metricName\":\"foo\", \"condition\":\"GreaterThanThreshold\", \"threshold\": 1.23}");

        CreateRuleRequest actual = CreateRuleRequestTranslator.from(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getMetricName(), "foo");
        Assert.assertEquals(actual.getCondition(), RuleCondition.GreaterThanThreshold);
        Assert.assertEquals(actual.getThreshold(), 1.23);
    }

}