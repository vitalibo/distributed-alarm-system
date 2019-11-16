package com.github.vitalibo.alarm.processor;

import com.github.vitalibo.alarm.processor.core.Spark;
import com.github.vitalibo.alarm.processor.core.Stream;
import com.github.vitalibo.alarm.processor.infrastructure.aws.Factory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Driver {

    private static final Factory factory = Factory.getInstance();

    public static void main(String[] args) throws Exception {
        final Stream stream;
        switch (args[0]) {
            case "alarm_stream":
                stream = factory.createAlarmStream();
                break;
            default:
                throw new IllegalArgumentException("Unknown stream name");
        }

        try (Spark spark = factory.createSpark()) {
            spark.submit(stream);

            spark.start();
            spark.awaitTermination();
        } catch (Exception e) {
            logger.error("Stream failed execution", e);
            throw e;
        }
    }
}
