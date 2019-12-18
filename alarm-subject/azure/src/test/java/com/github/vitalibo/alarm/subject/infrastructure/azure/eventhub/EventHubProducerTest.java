package com.github.vitalibo.alarm.subject.infrastructure.azure.eventhub;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EventHubProducerTest {

    @Mock
    private EventHubClient mockEventHubClient;
    @Captor
    private ArgumentCaptor<EventData> captorEventData;

    private EventHubProducer producer;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        producer = new EventHubProducer(mockEventHubClient);
    }

    @Test
    public void testSend() throws EventHubException {
        producer.send("foo");

        Mockito.verify(mockEventHubClient)
            .sendSync(captorEventData.capture(), Mockito.eq(String.valueOf("foo".hashCode())));
        EventData actual = captorEventData.getValue();
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getBytes(), "foo".getBytes());
    }

}