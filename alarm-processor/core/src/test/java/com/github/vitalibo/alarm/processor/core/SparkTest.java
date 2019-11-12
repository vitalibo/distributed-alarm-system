package com.github.vitalibo.alarm.processor.core;


import org.apache.spark.HashPartitioner;
import org.apache.spark.Partitioner;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SparkTest {

    @Mock
    private JavaStreamingContext mockJavaStreamingContext;
    @Mock
    private JavaSparkContext mockJavaSparkContext;
    @Mock
    private Stream mockStream;
    @Mock
    private Source<String> mockSource;
    @Mock
    private Sink<String> mockSink;
    @Mock
    private JavaDStream<String> mockJavaDStream;

    private Spark spySpark;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spySpark = Mockito.spy(new Spark(mockJavaStreamingContext));
    }

    @Test
    public void testSubmit() {
        spySpark.submit(mockStream);

        Mockito.verify(mockStream).process(spySpark);
    }

    @Test
    public void testCreateStream() {
        Mockito.when(mockSource.read(mockJavaStreamingContext))
            .thenReturn(mockJavaDStream);

        JavaDStream<String> actual = spySpark.createStream(mockSource);

        Assert.assertNotNull(actual);
        Mockito.verify(mockSource).read(mockJavaStreamingContext);
    }

    @Test
    public void testWriteStream() {
        spySpark.writeStream(mockSink, mockJavaDStream);

        Mockito.verify(mockSink).write(mockJavaStreamingContext, mockJavaDStream);
    }

    @Test
    public void testDefaultPartitioner() {
        Mockito.doReturn(2).when(spySpark).defaultParallelism();

        Partitioner actual = spySpark.defaultPartitioner();

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual instanceof HashPartitioner);
        Assert.assertEquals(actual.numPartitions(), 2);
        Mockito.verify(spySpark).defaultParallelism();
    }

    @Test
    public void testDefaultParallelism() {
        Mockito.doReturn(mockJavaSparkContext).when(spySpark).sparkContext();
        Mockito.when(mockJavaSparkContext.defaultParallelism()).thenReturn(2);

        Integer actual = spySpark.defaultParallelism();

        Assert.assertEquals(actual, (Integer) 2);
        Mockito.verify(spySpark).sparkContext();
        Mockito.verify(mockJavaSparkContext).defaultParallelism();
    }

}