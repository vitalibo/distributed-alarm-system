package com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub;

import com.github.vitalibo.alarm.processor.core.util.function.Function;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EventHubRecordsPublisherTest {

    @Mock
    private EventHubClient mockEventHubClient;
    @Mock
    private Function<String, EventData> mockRecordsTranslator;
    @Mock
    private EventData mockEventData;
    @Mock
    private EventHubRecordsPublisher mockEventHubRecordsPublisher;
    @Captor
    private ArgumentCaptor<Iterable<EventData>> captorIterable;

    private List<String> buffer;
    private EventHubRecordsPublisher<String> spyPublisher;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        buffer = new ArrayList<>();
        spyPublisher = Mockito.spy(new EventHubRecordsPublisher<>(
            3, mockEventHubClient, mockRecordsTranslator, buffer));
    }

    @Test
    public void testPublish() {
        Mockito.doAnswer(o -> {
            buffer.clear();
            return null;
        }).when(spyPublisher).flush();

        IntStream.range(0, 8).forEach(o -> spyPublisher.publish(String.valueOf(o)));

        Mockito.verify(spyPublisher, Mockito.times(2)).flush();
        Assert.assertEquals(buffer, Arrays.asList("6", "7"));
    }

    @Test
    public void testFlush() throws Exception {
        Mockito.when(mockRecordsTranslator.apply(Mockito.any())).thenReturn(mockEventData);
        Mockito.when(mockEventData.getBytes()).thenReturn("Foo".getBytes());
        buffer.add("foo");

        spyPublisher.flush();

        Mockito.verify(mockEventHubClient).send(captorIterable.capture());
        Iterable<EventData> value = captorIterable.getValue();
        Iterator<EventData> iterator = value.iterator();
        Assert.assertTrue(iterator.hasNext());
        EventData eventData = iterator.next();
        Assert.assertEquals(new String(eventData.getBytes()), "Foo");
        Assert.assertFalse(iterator.hasNext());
        Mockito.verify(mockRecordsTranslator).apply("foo");
        Assert.assertTrue(buffer.isEmpty());
    }

    @Test
    public void testFlushEmptyBuffer() {
        spyPublisher.flush();

        Mockito.verify(mockEventHubClient, Mockito.never()).send(Mockito.anyCollection());
    }

    @Test
    public void testClose() {
        Mockito.doNothing().when(spyPublisher).flush();

        spyPublisher.close();

        Mockito.verify(spyPublisher).flush();
    }

    @Test
    public void testCreateOrGet() {
        EventHubRecordsPublisher.Builder<String> spyBuilder =
            Mockito.spy(builder("foo"));
        Mockito.doReturn(mockEventHubRecordsPublisher)
            .when(spyBuilder).build(Mockito.anyInt(), Mockito.anyString(), Mockito.any());

        Set<EventHubRecordsPublisher<String>> actual =
            Stream.generate(spyBuilder::createOrGet)
                .limit(100).collect(Collectors.toSet());

        Assert.assertEquals(actual.size(), 1);
        Mockito.verify(spyBuilder, Mockito.times(100)).createOrGet();
        Mockito.verify(spyBuilder).build();
    }

    private EventHubRecordsPublisher.Builder<String> builder(String eventHub) {
        return new EventHubRecordsPublisher.Builder<String>()
            .withNamespaceName("foo")
            .withEventHubName(eventHub)
            .withSasKey("bar")
            .withTranslator(mockRecordsTranslator);
    }

}