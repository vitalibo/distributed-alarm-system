package com.github.vitalibo.alarm.subject.core.value.format;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class CsvValueFormatterTest {

    @Test
    public void testFormat() {
        CsvValueFormatter formatter = new CsvValueFormatter();
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("foo", "bar");
            put("baz", 1);
        }};

        String actual = formatter.format(map);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "bar,1");
    }

}