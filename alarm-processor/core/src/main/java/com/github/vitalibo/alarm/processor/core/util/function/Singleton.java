package com.github.vitalibo.alarm.processor.core.util.function;

import com.github.vitalibo.alarm.processor.core.util.ScalaTypes;
import scala.Tuple2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FunctionalInterface
public interface Singleton<T> extends Supplier<T> {

    @SuppressWarnings("unchecked")
    default T createOrGet() {
        Map<Tuple2<Class<?>, Integer>, Object> instances = Holder.instances;
        if (instances == null) {
            synchronized (Singleton.class) {
                instances = Holder.instances;
                if (instances == null) {
                    instances = Holder.instances = new ConcurrentHashMap<>();
                }
            }
        }

        return (T) instances.computeIfAbsent(
            ScalaTypes.tuple(this.getClass(), this.hashCode()),
            (o) -> get());
    }

    class Holder {

        private static volatile transient Map<Tuple2<Class<?>, Integer>, Object> instances;
    }
}