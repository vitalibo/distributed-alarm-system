package com.github.vitalibo.alarm.processor.core.model;

import com.github.vitalibo.alarm.processor.core.TestHelper;
import com.github.vitalibo.alarm.processor.core.util.Jackson;
import com.github.vitalibo.alarm.processor.core.util.Resources;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RuleTest {

    @Test
    public void testSerDe() {
        Rule expected = Jackson.fromJsonString(
            Resources.asString("/Rule.json"), Rule.class);

        Rule actual = TestHelper.serDe(expected);

        Assert.assertEquals(actual, expected);
    }

}