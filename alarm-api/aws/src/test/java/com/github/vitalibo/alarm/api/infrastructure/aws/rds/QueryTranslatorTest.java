package com.github.vitalibo.alarm.api.infrastructure.aws.rds;

import com.github.vitalibo.alarm.api.core.model.Rule;
import com.github.vitalibo.alarm.api.core.model.RuleCondition;
import com.github.vitalibo.alarm.api.core.util.Resources;
import com.github.vitalibo.alarm.api.infrastructure.aws.Factory;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;

public class QueryTranslatorTest {

    private Configuration configuration;

    @BeforeClass
    public void setUp() {
        configuration = new Configuration(Configuration.VERSION_2_3_29);
        configuration.setClassForTemplateLoading(Factory.class, "/");
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
    }

    @Test
    public void testFromCreateRule() throws IOException {
        QueryTranslator translator = new QueryTranslator(
            configuration.getTemplate("/mysql/CreateRule.ftl"));
        Rule rule = new Rule()
            .withMetricName("foo")
            .withCondition(RuleCondition.LessThanOrEqualToThreshold)
            .withThreshold(1.234);

        String actual = translator.from(rule);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, Resources.asString("/mysql/CreateRule.sql"));
    }

    @DataProvider
    public Object[][] samplesUpdateRuleById() {
        return new Object[][]{
            {"foo", "LessThanOrEqualToThreshold", 1.23, "All"},
            {"foo", null, null, "One"},
            {"foo", "LessThanOrEqualToThreshold", null, "Two"},
            {null, "LessThanOrEqualToThreshold", 1.23, "Three"},
            {null, null, 1.23, "Four"}
        };
    }

    @Test(dataProvider = "samplesUpdateRuleById")
    public void testFromUpdateRuleById(String metricName, String condition, Double threshold, String expected) throws IOException {
        QueryTranslator translator = new QueryTranslator(
            configuration.getTemplate("/mysql/UpdateRuleById.ftl"));
        Rule rule = new Rule()
            .withMetricName(metricName)
            .withCondition(condition == null ? null : RuleCondition.valueOf(condition))
            .withThreshold(threshold);

        String actual = translator.from(new HashMap<String, Object>() {{
            put("ruleId", "bar");
            put("rule", rule);
        }});

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, Resources.asString(String.format("/mysql/UpdateRuleById.%s.sql", expected)));
    }

    @Test
    public void testFromDeleteRuleById() throws IOException {
        QueryTranslator translator = new QueryTranslator(
            configuration.getTemplate("/mysql/DeleteRuleById.ftl"));

        String actual = translator.from(Collections.singletonMap("ruleId", "1"));

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, Resources.asString("/mysql/DeleteRuleById.sql"));
    }

    @Test
    public void testFromGetRuleById() throws IOException {
        QueryTranslator translator = new QueryTranslator(
            configuration.getTemplate("/mysql/GetRuleById.sql"));

        String actual = translator.from(Collections.singletonMap("ruleId", "1"));

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, Resources.asString("/mysql/GetRuleById.sql"));
    }

}