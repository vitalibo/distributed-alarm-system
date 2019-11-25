package com.github.vitalibo.alarm.api.core.model.transform;

import com.github.vitalibo.alarm.api.core.model.GetRuleRequest;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

public class GetRuleRequestTranslatorTest {

    @Test
    public void testFrom() {
        HttpRequest request = new HttpRequest();
        request.setQueryStringParameters(Collections.singletonMap("ruleId", "foo"));

        GetRuleRequest actual = GetRuleRequestTranslator.from(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getRuleId(), "foo");
    }

}