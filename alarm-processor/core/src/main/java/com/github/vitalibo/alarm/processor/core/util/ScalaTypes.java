package com.github.vitalibo.alarm.processor.core.util;

import scala.Tuple2;

public final class ScalaTypes {

    private ScalaTypes() {
    }

    public static <K, V> Tuple2<K, V> tuple(K key, V value) {
        return new Tuple2<>(key, value);
    }

}