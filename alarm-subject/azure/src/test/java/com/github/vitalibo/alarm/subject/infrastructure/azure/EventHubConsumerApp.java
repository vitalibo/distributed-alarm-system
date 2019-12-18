package com.github.vitalibo.alarm.subject.infrastructure.azure;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class EventHubConsumerApp {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage app.jar <eventhub-name>");
        }

        EventProcessorHost eventProcessorHost = createEventProcessorHost(args[0]);
        eventProcessorHost.registerEventProcessor(EventProcessor.class, new EventProcessorOptions()).get();
    }

    private static EventProcessorHost createEventProcessorHost(String eventHub) throws UnknownHostException {
        System.out.println("EventHub name: " + eventHub);

        Config config = ConfigFactory.load();
        return EventProcessorHost.EventProcessorHostBuilder
            .newBuilder(
                EventProcessorHost.createHostName(
                    InetAddress.getLocalHost()
                        .getHostAddress()),
                config.getString("azure.eventhub.consumer.groupname"))
            .useAzureStorageCheckpointLeaseManager(
                config.getString("azure.storage.connection.string"),
                config.getString("azure.storage.container.name"),
                eventHub)
            .useEventHubConnectionString(
                String.valueOf(new ConnectionStringBuilder()
                    .setNamespaceName(config.getString("azure.eventhub.namespace.name"))
                    .setEventHubName(eventHub)
                    .setSasKeyName(config.getString("azure.eventhub.sas.keyname"))
                    .setSasKey(config.getString("azure.eventhub.sas.key"))),
                eventHub)
            .build();
    }

    public static class EventProcessor implements IEventProcessor {

        private int checkpointBatchingCount = 0;

        @Override
        public void onOpen(PartitionContext context) {
            logger.trace("Partition {} is opening", context.getPartitionId());
        }

        @Override
        public void onClose(PartitionContext context, CloseReason reason) {
            logger.trace("Partition {} is closing for reason {}.", context.getPartitionId(), reason);
        }

        @Override
        public void onEvents(PartitionContext context, Iterable<EventData> events) throws Exception {
            logger.trace("Partition {} got event batch", context.getPartitionId());
            int eventCount = 0;
            for (EventData data : events) {
                System.out.println(new String(data.getBytes()));
                EventData.SystemProperties properties = data.getSystemProperties();
                logger.trace("Partition Id: {}, Offset: {}, SequenceNumber: {}",
                    context.getPartitionId(), properties.getOffset(), properties.getSequenceNumber());
                eventCount++;

                checkpointBatchingCount++;
                if (checkpointBatchingCount % 5 == 0) {
                    logger.trace("Partition {} checkpointing at {}:{}",
                        context.getPartitionId(), properties.getOffset(), properties.getSequenceNumber());
                    context.checkpoint(data).get();
                }
            }

            logger.trace("Partition {} batch size was {}  for host {}",
                context.getPartitionId(), eventCount, context.getOwner());
        }

        @Override
        public void onError(PartitionContext context, Throwable error) {
            logger.error("Partition {} onError: {}", context.getPartitionId(), error);
        }
    }

}