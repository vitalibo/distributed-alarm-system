package com.github.vitalibo.alarm.api.core.model.transform;

import com.github.vitalibo.alarm.api.core.model.DeleteRuleRequest;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

public class DeleteRuleRequestTranslatorTest {

    @Test
    public void testFrom() {
        HttpRequest request = new HttpRequest();
        request.setQueryStringParameters(Collections.singletonMap("ruleId", "foo"));

        DeleteRuleRequest actual = DeleteRuleRequestTranslator.from(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getRuleId(), "foo");
    }

}