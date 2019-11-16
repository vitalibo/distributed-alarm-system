package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClient;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.amazonaws.services.kinesis.model.PutRecordsResultEntry;
import com.github.vitalibo.alarm.processor.TestHelper;
import com.github.vitalibo.alarm.processor.core.util.function.Function;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class KinesisRecordsPublisherTest {

    @Mock
    private AmazonKinesisAsync mockAmazonKinesisAsync;
    @Mock
    private Function<String, PutRecordsRequestEntry> mockRecordsTranslator;
    @Mock
    private KinesisRecordsPublisher<String>.PutRecordsAsyncHandler mockPutRecordsAsyncHandler;
    @Captor
    private ArgumentCaptor<PutRecordsRequest> captorPutRecordsRequest;
    @Mock
    private PutRecordsRequestEntry mockPutRecordsRequestEntry;

    private List<String> buffer;
    private KinesisRecordsPublisher<String> spyPublisher;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        buffer = new ArrayList<>();
        spyPublisher = Mockito.spy(new KinesisRecordsPublisher<>(
            "kinesisStreamName", 3, mockAmazonKinesisAsync,
            mockRecordsTranslator, mockPutRecordsAsyncHandler, buffer));
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
        Mockito.when(mockRecordsTranslator.apply(Mockito.any())).thenReturn(mockPutRecordsRequestEntry);
        buffer.add("foo");

        spyPublisher.flush();

        Mockito.verify(mockAmazonKinesisAsync).putRecordsAsync(
            captorPutRecordsRequest.capture(), Mockito.eq(mockPutRecordsAsyncHandler));
        PutRecordsRequest request = captorPutRecordsRequest.getValue();
        Assert.assertEquals(request.getStreamName(), "kinesisStreamName");
        Assert.assertEquals(request.getRecords().size(), 1);
        Assert.assertEquals(request.getRecords().get(0), mockPutRecordsRequestEntry);
        Mockito.verify(mockRecordsTranslator).apply("foo");
        Assert.assertTrue(buffer.isEmpty());
    }

    @Test
    public void testFlushEmptyBuffer() {
        spyPublisher.flush();

        Mockito.verify(mockAmazonKinesisAsync, Mockito.never())
            .putRecordsAsync(Mockito.any(), Mockito.any());
    }

    @Test
    public void testClose() {
        Mockito.doNothing().when(spyPublisher).flush();

        spyPublisher.close();

        Mockito.verify(spyPublisher).flush();
    }

    @Test
    public void testCreateOrGet() {
        KinesisRecordsPublisher.Builder<String> spyBuilder =
            Mockito.spy(builder("kinesisStreamName"));

        Set<KinesisRecordsPublisher<String>> actual =
            Stream.generate(spyBuilder::createOrGet)
                .limit(100).collect(Collectors.toSet());

        Assert.assertEquals(actual.size(), 1);
        Mockito.verify(spyBuilder, Mockito.times(100)).createOrGet();
        Mockito.verify(spyBuilder).build();
    }

    @Test
    public void testMultiSingleton() {
        List<KinesisRecordsPublisher.Builder<String>> builders = Arrays.asList(
            builder("foo"), builder("foo"), builder("bar"),
            builder("bar"), builder("baz"));

        Set<KinesisRecordsPublisher> actual = builders.stream()
            .map(KinesisRecordsPublisher.Builder::createOrGet)
            .collect(Collectors.toSet());

        Assert.assertEquals(actual.size(), 3);
        Assert.assertEquals(builders.size(), 5);
    }

    @Test
    public void testBuilderSerDe() {
        KinesisRecordsPublisher.Builder<String> builder = builder("kinesisStreamName");

        KinesisRecordsPublisher.Builder<String> actual = TestHelper.serDe(builder);

        Assert.assertEquals(actual.createOrGet(), builder.createOrGet());
    }

    private KinesisRecordsPublisher.Builder<String> builder(String streamName) {
        return new KinesisRecordsPublisher.Builder<String>()
            .withStreamName(streamName)
            .withRegion("us-west-2")
            .withMaxBufferSize(10)
            .withRecordTranslator(mockRecordsTranslator)
            .withKinesisAsyncSupplier(() -> AmazonKinesisAsyncClient.asyncBuilder()
                .withRegion("us-west-2").build());
    }

    @Test
    public void testAsyncHandler() {
        KinesisRecordsPublisher<String>.PutRecordsAsyncHandler handler =
            spyPublisher.new PutRecordsAsyncHandler(3);

        handler.onSuccess(new PutRecordsRequest(), new PutRecordsResult().withFailedRecordCount(0));

        Mockito.verify(mockAmazonKinesisAsync, Mockito.never()).putRecordsAsync(Mockito.any(), Mockito.any());
    }

    @Test
    public void testRetryAsyncHandler() {
        PutRecordsRequest request = new PutRecordsRequest()
            .withStreamName("streamName")
            .withRecords(record("foo"), record("bar"), record("baz"));
        PutRecordsResult result = new PutRecordsResult()
            .withRecords(result("shard-01"), result(null), result("shard-02"))
            .withFailedRecordCount(1);
        KinesisRecordsPublisher<String>.PutRecordsAsyncHandler handler =
            spyPublisher.new PutRecordsAsyncHandler(3);

        handler.onSuccess(request, result);

        Mockito.verify(mockAmazonKinesisAsync).putRecordsAsync(
            Mockito.eq(new PutRecordsRequest()
                .withStreamName("streamName")
                .withRecords(record("bar"))),
            Mockito.any());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testFailRetryAsyncHandler() {
        KinesisRecordsPublisher<String>.PutRecordsAsyncHandler handler =
            spyPublisher.new PutRecordsAsyncHandler(0);

        handler.onSuccess(new PutRecordsRequest(), new PutRecordsResult().withFailedRecordCount(1));
    }

    private static PutRecordsRequestEntry record(String data) {
        return new PutRecordsRequestEntry()
            .withData(ByteBuffer.wrap(data.getBytes()));
    }

    private static PutRecordsResultEntry result(String shardId) {
        return new PutRecordsResultEntry()
            .withShardId(shardId);
    }

}