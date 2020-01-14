package com.github.vitalibo.alarm.processor.infrastructure.azure.cosmosdb;

import com.github.vitalibo.alarm.processor.core.Source;
import com.github.vitalibo.alarm.processor.core.model.EventLog;
import com.github.vitalibo.alarm.processor.core.util.function.Function;
import lombok.AllArgsConstructor;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Iterator;

@AllArgsConstructor
public class EventLogSource<T> implements Source<EventLog<T>> {

    private final Source<ChangeFeed> delegate;
    private final Function<ChangeFeed, Iterator<EventLog<T>>> translator;

    @Override
    public JavaDStream<EventLog<T>> read(JavaStreamingContext context) {
        return delegate.read(context)
            .flatMap(translator::apply);
    }

}
