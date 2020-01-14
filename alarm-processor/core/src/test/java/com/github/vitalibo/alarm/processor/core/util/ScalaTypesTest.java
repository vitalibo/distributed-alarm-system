package com.github.vitalibo.alarm.processor.core.util;

import org.testng.Assert;
import org.testng.annotations.Test;
import scala.Function1;
import scala.Tuple2;
import scala.collection.immutable.Map;
import scala.reflect.ClassTag;

import java.util.Collections;

public class ScalaTypesTest {

    @Test
    public void testClassTag() {
        ClassTag<String> actual = ScalaTypes.classTag(String.class);

        Assert.assertNotNull(actual);
    }

    @Test
    public void testFunction() {
        Function1<String, String> actual = ScalaTypes.function(String::toUpperCase);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.apply("foo"), "FOO");
    }

    @Test
    public void testTuple() {
        Tuple2<String, String> actual = ScalaTypes.tuple("foo", "bar");

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual._1, "foo");
        Assert.assertEquals(actual._2, "bar");
    }

    @Test
    public void testAsImmutableMap() {
        Map<String, String> actual = ScalaTypes.asImmutableMap(Collections.singletonMap("foo", "bar"));

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual instanceof scala.collection.immutable.Map);
        Assert.assertEquals(actual.get("foo").get(), "bar");
    }

}