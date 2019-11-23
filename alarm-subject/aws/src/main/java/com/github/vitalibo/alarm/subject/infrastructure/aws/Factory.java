package com.github.vitalibo.alarm.subject.infrastructure.aws;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.github.vitalibo.alarm.subject.core.GenericValueParser;
import com.github.vitalibo.alarm.subject.core.Producer;
import com.github.vitalibo.alarm.subject.core.Subject;
import com.github.vitalibo.alarm.subject.core.SubjectExecutor;
import com.github.vitalibo.alarm.subject.core.producer.StandardOutputProducer;
import com.github.vitalibo.alarm.subject.infrastructure.aws.kinesis.KinesisProducer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Factory {

    private static final String AWS_REGION = "aws.region";
    private static final String AWS_PROFILE = "aws.profile";
    private static final String AWS_KINESIS_STREAM_NAME = "aws.kinesis.streamName";

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

    public Producer createKinesisProducer() {
        return new KinesisProducer(
            config.getString(AWS_KINESIS_STREAM_NAME),
            AmazonKinesisClient.builder()
                .withRegion(config.getString(AWS_REGION))
                .withCredentials(config.hasPath(AWS_PROFILE) ?
                    new ProfileCredentialsProvider(config.getString(AWS_PROFILE)) :
                    new DefaultAWSCredentialsProviderChain())
                .build());
    }

    public Producer createStandardOutputProducer() {
        return new StandardOutputProducer();
    }

}