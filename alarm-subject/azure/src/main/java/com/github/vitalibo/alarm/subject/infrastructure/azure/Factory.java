package com.github.vitalibo.alarm.subject.infrastructure.azure;

import com.github.vitalibo.alarm.subject.core.GenericValueParser;
import com.github.vitalibo.alarm.subject.core.Producer;
import com.github.vitalibo.alarm.subject.core.Subject;
import com.github.vitalibo.alarm.subject.core.SubjectExecutor;
import com.github.vitalibo.alarm.subject.core.producer.StandardOutputProducer;
import com.github.vitalibo.alarm.subject.infrastructure.azure.eventhub.EventHubProducer;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Factory {

    private static final String AZURE_EVENTHUB_NAMESPACE_NAME = "azure.eventhub.namespace.name";
    private static final String AZURE_EVENTHUB_NAME = "azure.eventhub.hub.name";
    private static final String AZURE_EVENTHUB_SHARED_ACCESS_POLICY_KEY_NAME = "azure.eventhub.sas.keyname";
    private static final String AZURE_EVENTHUB_SHARED_ACCESS_POLICY_KEY = "azure.eventhub.sas.key";

    private static final String SUBJECT_ROOT = "subject";
    private static final String SUBJECT_MAX_MESSAGE_NUMBER = "maxMessageNumber";
    private static final String SUBJECT_DELAY_RATE = "delayRate";

    @Getter(lazy = true)
    private static final Factory instance = new Factory(ConfigFactory.load(), ConfigFactory.parseResources("default-application.conf"));

    private final Config config;

    Factory(Config... configs) {
        this.config = Arrays.stream(configs)
            .reduce(Config::withFallback)
            .orElseThrow(IllegalStateException::new)
            .resolve();
    }

    public SubjectExecutor createExecutorService() {
        return new SubjectExecutor(
            config.getObjectList(SUBJECT_ROOT)
                .size());
    }

    public List<Subject> createSubjects(Producer producer) {
        return config.getObjectList(SUBJECT_ROOT)
            .stream()
            .map(o -> createSubject(o.toConfig(), producer))
            .collect(Collectors.toList());
    }

    private Subject createSubject(Config config, Producer producer) {
        return new Subject.Builder()
            .withGenericValue(GenericValueParser.parse(config))
            .withMaxMessageNumber(config.hasPath(SUBJECT_MAX_MESSAGE_NUMBER) ? config.getLong(SUBJECT_MAX_MESSAGE_NUMBER) : -1)
            .withDelayRate(config.hasPath(SUBJECT_DELAY_RATE) ? config.getLong(SUBJECT_DELAY_RATE) : 1000)
            .withProducer(producer)
            .build();
    }

    @SneakyThrows
    public Producer createEventHubProducer() {
        return new EventHubProducer(
            EventHubClient.createFromConnectionStringSync(
                String.valueOf(new ConnectionStringBuilder()
                    .setNamespaceName(config.getString(AZURE_EVENTHUB_NAMESPACE_NAME))
                    .setEventHubName(config.getString(AZURE_EVENTHUB_NAME))
                    .setSasKeyName(config.getString(AZURE_EVENTHUB_SHARED_ACCESS_POLICY_KEY_NAME))
                    .setSasKey(config.getString(AZURE_EVENTHUB_SHARED_ACCESS_POLICY_KEY))),
                Executors.newScheduledThreadPool(1)));
    }

    public Producer createStandardOutputProducer() {
        return new StandardOutputProducer();
    }

}