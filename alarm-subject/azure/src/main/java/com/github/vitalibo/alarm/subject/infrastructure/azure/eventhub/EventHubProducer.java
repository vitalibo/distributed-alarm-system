package com.github.vitalibo.alarm.subject.infrastructure.azure.eventhub;

import com.github.vitalibo.alarm.subject.core.Producer;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class EventHubProducer implements Producer {

    private final EventHubClient client;

    @Override
    @SneakyThrows
    public synchronized void send(String value) {
        EventData event = EventData.create(value.getBytes());
        client.sendSync(event, String.valueOf(value.hashCode()));
    }

}