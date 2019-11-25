package com.github.vitalibo.alarm.api.core.model.transform;

import com.github.vitalibo.alarm.api.core.model.GetRuleResponse;
import com.github.vitalibo.alarm.api.core.model.Rule;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import com.github.vitalibo.alarm.api.core.model.Status;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GetRuleResponseTranslatorTest {

    @Test
    public void testFrom() {
        Rule rule = new Rule()
            .withRuleId("ruleId")
            .withMetricName("metricName")
            .withCondition(RuleCondition.GreaterThanThreshold)
            .withThreshold(1.234);

        GetRuleResponse actual = GetRuleResponseTranslator.from(rule, Status.Pending);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getMetricName(), "metricName");
        Assert.assertEquals(actual.getCondition(), RuleCondition.GreaterThanThreshold);
        Assert.assertEquals(actual.getThreshold(), 1.234);
        Assert.assertEquals(actual.getStatus(), Status.Pending);
    }

}