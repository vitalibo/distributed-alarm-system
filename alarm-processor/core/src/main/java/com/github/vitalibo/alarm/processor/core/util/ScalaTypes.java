package com.github.vitalibo.alarm.processor.core.util;

import com.github.vitalibo.alarm.processor.core.util.function.Function;
import lombok.RequiredArgsConstructor;
import scala.*;
import scala.collection.JavaConverters;
import scala.collection.immutable.Map;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.AbstractFunction1;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class ScalaTypes {

    private ScalaTypes() {
    }

    public static <T> ClassTag<T> classTag(Class<T> cls) {
        return ClassTag$.MODULE$.apply(cls);
    }

    public static <T1, R> Function1<T1, R> function(Function<T1, R> function) {
        return new Function1Wrapper<>(function);
    }

    public static <K, V> Tuple2<K, V> tuple(K k, V v) {
        return new Tuple2<>(k, v);
    }

    public static <K1, K2, K3> Tuple3<K1, K2, K3> tuple(K1 k1, K2 k2, K3 k3) {
        return new Tuple3<>(k1, k2, k3);
    }

    public static String classes(Class<?>... cls) {
        return Arrays.stream(cls)
            .map(Class::getName)
            .collect(Collectors.joining(","));
    }

    public static <K, V> Map<K, V> asImmutableMap(java.util.Map<K, V> map) {
        return JavaConverters.mapAsScalaMapConverter(map)
            .asScala()
            .toMap(Predef.conforms());
    }

    @RequiredArgsConstructor
    private static class Function1Wrapper<T1, R> extends AbstractFunction1<T1, R> implements Serializable {
        private final Function<T1, R> delegate;

        @Override
        public R apply(T1 t1) {
            return delegate.apply(t1);
        }
    }

}