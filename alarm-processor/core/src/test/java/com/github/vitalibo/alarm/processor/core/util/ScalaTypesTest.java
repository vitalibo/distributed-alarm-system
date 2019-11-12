package com.github.vitalibo.alarm.processor.core.util;

import org.testng.Assert;
import org.testng.annotations.Test;
import scala.Tuple2;

public class ScalaTypesTest {

    @Test
    public void testTuple() {
        Tuple2<String, String> actual = ScalaTypes.tuple("foo", "bar");

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual._1, "foo");
        Assert.assertEquals(actual._2, "bar");
    }

}