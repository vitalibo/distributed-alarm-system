package com.github.vitalibo.alarm.processor.infrastructure.aws.kinesis.transform;

import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.github.vitalibo.alarm.processor.core.model.Alarm;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PutRecordsRequestEntryTranslatorTest {

    @Test
    public void testFromAlarm() {
        Alarm alarm = new Alarm()
            .withMetricName("foo")
            .withRuleId("bar")
            .withState(Alarm.State.Ok);

        PutRecordsRequestEntry actual = PutRecordsRequestEntryTranslator.fromAlarm(alarm);

        Assert.assertNotNull(actual);
        Assert.assertEquals(
            new String(actual.getData().array()), "{\"metricName\":\"foo\",\"ruleId\":\"bar\",\"state\":\"Ok\"}\n");
        Assert.assertEquals(actual.getPartitionKey(), "bar");
    }

}