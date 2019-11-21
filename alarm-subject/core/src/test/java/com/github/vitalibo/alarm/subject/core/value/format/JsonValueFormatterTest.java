package com.github.vitalibo.alarm.subject.core.value.format;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class JsonValueFormatterTest {

    @Test
    public void testFormat() {
        JsonValueFormatter formatter = new JsonValueFormatter();
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("foo", "bar");
            put("baz", 1);
        }};

        String actual = formatter.format(map);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "{\"foo\":\"bar\",\"baz\":1}");
    }

}