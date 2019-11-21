package com.github.vitalibo.alarm.subject.core;

import lombok.RequiredArgsConstructor;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class SubjectExecutor implements Closeable {

    private final ExecutorService executor;
    private final List<CompletableFuture<Void>> futures;

    public SubjectExecutor(int size) {
        this(Executors.newFixedThreadPool(size), new ArrayList<>());
    }

    public void submit(Runnable runnable) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(runnable, executor);
        futures.add(future);
    }

    public void awaitTermination() {
        if (futures.isEmpty()) {
            return;
        }

        CompletableFuture
            .allOf(futures.toArray(new CompletableFuture[]{}))
            .join();
    }

    @Override
    public void close() {
        executor.shutdown();
    }

}