package com.github.vitalibo.alarm.processor.infrastructure.azure.eventhub;

import org.apache.spark.eventhubs.EventHubsConf;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EventHubStreamSourceTest {

    @Mock
    private JavaStreamingContext mockJavaStreamingContext;
    @Mock
    private JavaDStream<String> mockJavaDStream;
    @Captor
    private ArgumentCaptor<EventHubsConf> captorEventHubsConf;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRead() {
        EventHubStreamSource<String> spySource = Mockito.spy(makeSample());
        Mockito.doReturn(mockJavaDStream)
            .when(spySource).read(Mockito.eq(mockJavaStreamingContext), Mockito.any());

        JavaDStream<String> actual = spySource.read(mockJavaStreamingContext);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, mockJavaDStream);
        Mockito.verify(spySource).read(Mockito.eq(mockJavaStreamingContext), captorEventHubsConf.capture());
        EventHubsConf conf = captorEventHubsConf.getValue();
        Assert.assertEquals(conf.apply(EventHubsConf.ConsumerGroupKey()), "$default");
        Assert.assertEquals(conf.apply(EventHubsConf.ConnectionStringKey()),
            "Endpoint=sb://foo.servicebus.windows.net;EntityPath=bar;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=baz");
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "namespaceName")
    public void testMissingNamespace() {
        EventHubStreamSource<String> actual = makeSample()
            .withNamespaceName(null);

        actual.read(mockJavaStreamingContext);
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "eventHubName")
    public void testMissingHubName() {
        EventHubStreamSource<String> actual = makeSample()
            .withEventHubName(null);

        actual.read(mockJavaStreamingContext);
    }

    @Test
    public void testDefaultSasName() {
        EventHubStreamSource<String> actual = Mockito.spy(makeSample());
        Mockito.doReturn(mockJavaDStream)
            .when(actual).read(Mockito.eq(mockJavaStreamingContext), Mockito.any());

        actual.read(mockJavaStreamingContext);
        Assert.assertEquals(actual.getSasKeyName(), "RootManageSharedAccessKey");
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "sasKeyName")
    public void testMissingSasName() {
        EventHubStreamSource<String> actual = makeSample()
            .withSasKeyName(null);

        actual.read(mockJavaStreamingContext);
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "sasKey")
    public void testMissingSasKey() {
        EventHubStreamSource<String> actual = makeSample()
            .withSasKey(null);

        actual.read(mockJavaStreamingContext);
    }

    @Test
    public void testDefaultConsumerGroupKey() {
        EventHubStreamSource<String> actual = Mockito.spy(makeSample());
        Mockito.doReturn(mockJavaDStream)
            .when(actual).read(Mockito.eq(mockJavaStreamingContext), Mockito.any());

        actual.read(mockJavaStreamingContext);
        Assert.assertEquals(actual.getConsumerGroupKey(), "$default");
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "consumerGroupKey")
    public void testMissingConsumerGroupKey() {
        EventHubStreamSource<String> actual = makeSample()
            .withConsumerGroupKey(null);

        actual.read(mockJavaStreamingContext);
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "translator")
    public void testMissingTranslator() {
        EventHubStreamSource<String> actual = makeSample()
            .withTranslator(null);

        actual.read(mockJavaStreamingContext);
    }

    private static EventHubStreamSource<String> makeSample() {
        return new EventHubStreamSource<String>()
            .withNamespaceName("foo")
            .withEventHubName("bar")
            .withSasKey("baz")
            .withTranslator(o -> new String(o.getBytes()));
    }

}