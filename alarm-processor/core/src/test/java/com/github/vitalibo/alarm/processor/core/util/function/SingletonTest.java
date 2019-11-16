package com.github.vitalibo.alarm.processor.core.util.function;

import com.github.vitalibo.alarm.processor.core.TestHelper;
import lombok.Data;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SingletonTest {

    @Test
    public void testCreateOrGet() {
        Singleton<O> singleton = new A("foo0");

        Assert.assertNotSame(singleton.get(), singleton.get());
        Assert.assertSame(singleton.createOrGet(), singleton.createOrGet());
    }

    @Test
    public void testDifferenceType() {
        Singleton<O> singleton0 = new A("foo1");
        Singleton<O> singleton1 = new B("foo1");

        Assert.assertNotSame(singleton0.createOrGet(), singleton1.createOrGet());
    }

    @Test
    public void testSameType() {
        Singleton<O> singleton0 = new A("foo2");
        Singleton<O> singleton1 = new A("foo2");

        Assert.assertSame(singleton0.createOrGet(), singleton1.createOrGet());
    }

    @Test
    public void testDifferenceHashCodeSameType() {
        Singleton<O> singleton0 = new A("foo3");
        Singleton<O> singleton1 = new A("bar3");

        Assert.assertNotSame(singleton0.createOrGet(), singleton1.createOrGet());
    }

    @Test
    public void testThreadSafe() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Callable<O> callable = () -> new A("foo4").createOrGet();
        List<Future<O>> futures = IntStream.range(0, 1000)
            .parallel().mapToObj(o -> executor.submit(callable))
            .collect(Collectors.toList());

        Set<O> actual = new HashSet<>();
        for (Future<O> future : futures) {
            actual.add(future.get());
        }

        executor.shutdown();
        Assert.assertEquals(actual.size(), 1);
        Assert.assertTrue(actual.contains(callable.call()));
    }

    @Test
    public void testSerDe() {
        Singleton<O> expected = new A("foo5");

        Singleton<O> actual = TestHelper.serDe(expected);

        Assert.assertSame(actual.createOrGet(), expected.createOrGet());
    }

    @Data
    public static class A implements Singleton<O> {

        private final String value;

        @Override
        public O get() {
            return new O(Math.random());
        }
    }

    @Data
    public static class B implements Singleton<O> {

        private final String value;

        @Override
        public O get() {
            return new O(Math.random());
        }
    }

    @Data
    private static class O {

        private final Object value;
    }
}