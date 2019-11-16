package com.github.vitalibo.alarm.processor.infrastructure.aws.dms;

import com.github.vitalibo.alarm.processor.core.Source;
import com.github.vitalibo.alarm.processor.core.util.function.Function;
import lombok.AllArgsConstructor;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Iterator;

@AllArgsConstructor
public class EventLogSource<T> implements Source<T> {

    private final Source<DMSEvent> delegate;
    private final Function<DMSEvent, Iterator<T>> translator;

    @Override
    public JavaDStream<T> read(JavaStreamingContext context) {
        return delegate.read(context)
            .flatMap(translator::apply);
    }

}