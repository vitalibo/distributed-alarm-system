package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis;


import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.model.*;
import com.github.vitalibo.alarm.processor.TestHelper;
import com.github.vitalibo.alarm.processor.core.util.ScalaTypes;
import com.github.vitalibo.alarm.processor.core.util.function.Function;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kinesis.KinesisInitialPositions;
import org.apache.spark.streaming.kinesis.KinesisInputDStream;
import org.mockito.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import scala.Option;

import java.util.Arrays;
import java.util.Collections;

public class KinesisStreamSourceTest {

    @Mock
    private JavaStreamingContext mockJavaStreamingContext;
    @Mock
    private AmazonKinesis mockAmazonKinesis;
    @Mock
    private KinesisStreamSource.StreamBuilder mockStreamBuilder;
    @Mock
    private Function<Record, String> mockTranslator;
    @Captor
    private ArgumentCaptor<DescribeStreamRequest> captorDescribeStreamRequest;
    @Mock
    private DescribeStreamResult mockDescribeStreamResult;
    @Mock
    private StreamDescription mockStreamDescription;
    @Mock
    private Shard mockShard;
    @Mock
    private JavaDStream<Record> mockJavaDStreamRecord;
    @Mock
    private JavaDStream<String> mockJavaDStream;
    @Captor
    private ArgumentCaptor<KinesisInputDStream.Builder> captorKinesisInputDStreamBuilder;

    private KinesisStreamSource<String> source;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        source = new KinesisStreamSource<>(mockAmazonKinesis, mockStreamBuilder, mockTranslator);
        Mockito.when(mockAmazonKinesis.describeStream(Mockito.any(DescribeStreamRequest.class)))
            .thenReturn(mockDescribeStreamResult);
        Mockito.when(mockDescribeStreamResult.getStreamDescription()).thenReturn(mockStreamDescription);
        Mockito.when(mockStreamBuilder.getStreamName()).thenReturn("kinesisStreamName");
        Mockito.when(mockStreamBuilder.build(Mockito.any())).thenReturn(mockJavaDStreamRecord);
        Mockito.when(mockJavaDStreamRecord.union(Mockito.any())).thenReturn(mockJavaDStreamRecord);
        Mockito.when(mockJavaDStreamRecord.filter(Mockito.any())).thenReturn(mockJavaDStreamRecord);
        Mockito.when(mockJavaDStreamRecord.map(mockTranslator)).thenReturn(mockJavaDStream);
    }

    @Test
    public void testReadMultiShards() {
        Mockito.when(mockStreamDescription.getShards()).thenReturn(Arrays.asList(mockShard, mockShard));

        JavaDStream<String> actual = source.read(mockJavaStreamingContext);

        Assert.assertNotNull(actual);
        Mockito.verify(mockAmazonKinesis).describeStream(captorDescribeStreamRequest.capture());
        Assert.assertEquals(captorDescribeStreamRequest.getValue().getStreamName(), "kinesisStreamName");
        Mockito.verify(mockStreamBuilder, Mockito.times(2)).build(mockJavaStreamingContext);
        Mockito.verify(mockJavaDStreamRecord).union(mockJavaDStreamRecord);
        Mockito.verify(mockJavaDStreamRecord).map(mockTranslator);
    }

    @Test
    public void testReadSingleShard() {
        Mockito.when(mockStreamDescription.getShards()).thenReturn(Collections.singletonList(mockShard));

        JavaDStream<String> actual = source.read(mockJavaStreamingContext);

        Assert.assertNotNull(actual);
        Mockito.verify(mockAmazonKinesis).describeStream(captorDescribeStreamRequest.capture());
        Assert.assertEquals(captorDescribeStreamRequest.getValue().getStreamName(), "kinesisStreamName");
        Mockito.verify(mockStreamBuilder).build(mockJavaStreamingContext);
        Mockito.verify(mockJavaDStreamRecord, Mockito.never()).union(mockJavaDStreamRecord);
        Mockito.verify(mockJavaDStreamRecord).map(mockTranslator);
    }

    @Test
    public void testBuild() {
        KinesisStreamSource.StreamBuilder spyBuilder = Mockito.spy(
            new KinesisStreamSource.StreamBuilder()
                .withStreamName("kinesisStreamName")
                .withRegion("us-west-2")
                .withApplicationName("kinesisCheckpointApplicationName")
                .withInitialPosition(InitialPositionInStream.LATEST)
                .withCheckpointInterval(new Duration(100)));
        Mockito.doReturn(mockJavaDStreamRecord)
            .when(spyBuilder).build(Mockito.any(), Mockito.any());

        spyBuilder.build(mockJavaStreamingContext);

        Mockito.verify(spyBuilder).build(captorKinesisInputDStreamBuilder.capture(),
            Mockito.eq(ScalaTypes.classTag(Record.class)));
        KinesisInputDStream.Builder actual = captorKinesisInputDStreamBuilder.getValue();

        class ReflectionHelper extends TestHelper.Reflection<KinesisInputDStream.Builder> {
            ReflectionHelper() {
                super(KinesisInputDStream.Builder.class, actual);
            }

            @Override
            public <K> K field(String fieldName) {
                Option<K> v = super.field(fieldName);
                return v.get();
            }
        }

        ReflectionHelper ref = new ReflectionHelper();
        Assert.assertEquals(ref.field("streamName"), "kinesisStreamName");
        Assert.assertEquals(ref.field("endpointUrl"), "https://kinesis.us-west-2.amazonaws.com");
        Assert.assertEquals(ref.field("checkpointAppName"), "kinesisCheckpointApplicationName");
        Assert.assertEquals(ref.field("regionName"), "us-west-2");
        Assert.assertTrue(ref.field("initialPosition") instanceof KinesisInitialPositions.Latest);
        Assert.assertEquals(ref.field("checkpointInterval"), new Duration(100));
        Assert.assertEquals(ref.field("storageLevel"), StorageLevel.MEMORY_AND_DISK_2());
    }

}