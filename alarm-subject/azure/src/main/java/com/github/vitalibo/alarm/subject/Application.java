package com.github.vitalibo.alarm.subject;

import com.github.vitalibo.alarm.subject.core.Producer;
import com.github.vitalibo.alarm.subject.core.Subject;
import com.github.vitalibo.alarm.subject.core.SubjectExecutor;
import com.github.vitalibo.alarm.subject.infrastructure.azure.Factory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {

    private static final Factory factory = Factory.getInstance();

    public static void main(String[] args) {
        try (SubjectExecutor executor = factory.createExecutorService()) {
            final Producer producer;
            switch (args[0]) {
                case "stdout":
                    producer = factory.createStandardOutputProducer();
                    break;
                case "eventhub":
                    producer = factory.createEventHubProducer();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported producer type.");
            }

            for (Subject subject : factory.createSubjects(producer)) {
                executor.submit(subject);
            }

            executor.awaitTermination();
        } catch (Exception e) {
            logger.error("Application failed execution", e);
            throw e;
        }
    }

}