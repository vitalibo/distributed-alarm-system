package com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub;

import com.github.vitalibo.alarm.processor.core.Source;
import com.github.vitalibo.alarm.processor.core.util.ScalaTypes;
import com.github.vitalibo.alarm.processor.core.util.function.Function;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;
import org.apache.spark.eventhubs.EventHubsConf;
import org.apache.spark.eventhubs.EventHubsConf$;
import org.apache.spark.eventhubs.EventHubsUtils;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.HashMap;
import java.util.Objects;

@Data
@Wither
@RequiredArgsConstructor
public class EventHubStreamSource<T> implements Source<T> {

    private final String namespaceName;
    private final String eventHubName;
    private final String sasKeyName;
    private final String sasKey;
    private final String consumerGroupKey;
    private final Function<EventData, T> translator;

    public EventHubStreamSource() {
        this(null, null, "RootManageSharedAccessKey", null, "$default", null);
    }

    @Override
    public JavaDStream<T> read(JavaStreamingContext context) {
        Objects.requireNonNull(namespaceName, "namespaceName");
        Objects.requireNonNull(eventHubName, "eventHubName");
        Objects.requireNonNull(sasKeyName, "sasKeyName");
        Objects.requireNonNull(sasKey, "sasKey");
        Objects.requireNonNull(consumerGroupKey, "consumerGroupKey");
        Objects.requireNonNull(translator, "translator");

        EventHubsConf configuration = EventHubsConf$.MODULE$.toConf(
            ScalaTypes.asImmutableMap(new HashMap<String, String>() {{
                put(EventHubsConf.ConnectionStringKey(), String.valueOf(
                    new ConnectionStringBuilder()
                        .setNamespaceName(namespaceName)
                        .setEventHubName(eventHubName)
                        .setSasKeyName(sasKeyName)
                        .setSasKey(sasKey)));
                put(EventHubsConf.ConsumerGroupKey(), consumerGroupKey);
            }}));

        return read(context, configuration);
    }

    JavaDStream<T> read(JavaStreamingContext context, EventHubsConf configuration) {
        return EventHubsUtils.createDirectStream(context, configuration)
            .map(translator::apply);
    }

}