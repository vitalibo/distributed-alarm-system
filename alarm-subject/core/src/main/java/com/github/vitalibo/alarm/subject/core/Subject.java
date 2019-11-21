package com.github.vitalibo.alarm.subject.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Subject implements Runnable {

    private final GenericValue genericValue;
    private final Producer producer;
    private final long delayRate;
    private final long maxMessageNumber;

    @Override
    @SneakyThrows
    public void run() {
        final String subjectId = Integer.toHexString(hashCode());
        long sentMessages = 0;
        while (sentMessages < maxMessageNumber || maxMessageNumber == -1) {
            long before = System.currentTimeMillis();
            producer.send(String.valueOf(genericValue.get()));
            long after = System.currentTimeMillis();
            sentMessages++;

            long delay = delayRate - (after - before);
            if (delay > 0) {
                Thread.sleep(delay);
            }

            logger.debug("CurrentTime: {}, Subject: {}, SendMessage: #{}, Delay: {} ms.",
                Instant.ofEpochMilli(after), subjectId, sentMessages, delay);
        }
    }

    @Wither
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        private final GenericValue genericValue;
        private final Producer producer;
        private final long delayRate;
        private final long maxMessageNumber;

        public Builder() {
            this(null, null, 100, -1);
        }

        public Subject build() {
            Objects.requireNonNull(genericValue, "`genericValue` must be defined.");
            Objects.requireNonNull(producer, "`producer` must be defined.");
            if (delayRate < 0) throw new IllegalArgumentException("`delayRate` must be more or equals 0");
            if (maxMessageNumber < -1) throw new IllegalArgumentException("`maxMessageNumber` must be more or equals 1.");

            return new Subject(genericValue, producer, delayRate, maxMessageNumber);
        }

    }

}