package com.github.vitalibo.alarm.subject.infrastructure.aws;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.*;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class KinesisConsumerApp {

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage app.jar <streamName> <profile> <region>");
        }

        final Iterable<Record> recordIterable = () -> new KinesisRecordIterator(args[0], args[1], args[2]);
        for (Record record : recordIterable) {
            System.out.println(new String(record.getData().array()));
        }
    }

    private static class KinesisRecordIterator implements Iterator<Record> {

        private final AmazonKinesis kinesis;
        private final List<String> shardIterators;
        private final List<Record> records = new ArrayList<>();

        KinesisRecordIterator(String streamName, String profile, String region) {
            System.out.println("StreamName: " + streamName);
            System.out.println("Profile: " + profile);
            System.out.println("Region: " + region);

            this.kinesis = AmazonKinesisClient.builder()
                .withRegion(region)
                .withCredentials(new ProfileCredentialsProvider(profile))
                .build();
            this.shardIterators = shardIterators(kinesis, streamName);
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        @SneakyThrows
        public Record next() {
            if (!records.isEmpty()) {
                return records.remove(0);
            }

            for (int i = 0; i < shardIterators.size(); i++) {
                GetRecordsResult result = kinesis.getRecords(new GetRecordsRequest()
                    .withShardIterator(shardIterators.get(i))
                    .withLimit(12));

                shardIterators.set(i, result.getNextShardIterator());
                records.addAll(result.getRecords());
            }

            if (records.isEmpty()) {
                Thread.sleep(250);
            }

            return next();
        }

        private static List<String> shardIterators(AmazonKinesis kinesis, String streamName) {
            final List<Shard> shards = kinesis.describeStream(new DescribeStreamRequest()
                .withStreamName(streamName))
                .getStreamDescription()
                .getShards();

            return shards.stream()
                .map(shard -> new GetShardIteratorRequest()
                    .withStreamName(streamName)
                    .withShardId(shard.getShardId())
                    .withShardIteratorType(ShardIteratorType.LATEST))
                .map(kinesis::getShardIterator)
                .map(GetShardIteratorResult::getShardIterator)
                .collect(Collectors.toList());
        }

    }

}