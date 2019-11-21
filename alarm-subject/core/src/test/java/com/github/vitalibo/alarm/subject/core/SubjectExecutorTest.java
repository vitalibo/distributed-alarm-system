package com.github.vitalibo.alarm.subject.core;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SubjectExecutorTest {

    @Mock
    private ExecutorService mockExecutorService;
    @Mock
    private CompletableFuture<Void> mockCompletableFuture;
    @Mock
    private Runnable mockRunnable;

    private List<CompletableFuture<Void>> futures;
    private SubjectExecutor executor;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        futures = new ArrayList<>();
        executor = new SubjectExecutor(mockExecutorService, futures);
    }

    @Test
    public void testSubmit() {
        executor = new SubjectExecutor(Executors.newFixedThreadPool(1), futures);
        Mockito.doAnswer(answer -> {
            Thread.sleep(1000);
            return answer;
        }).when(mockRunnable).run();

        executor.submit(mockRunnable);

        Assert.assertFalse(futures.isEmpty());
        while (!futures.get(0).isDone()) { }
        Mockito.verify(mockRunnable).run();
    }

    @Test
    public void testAwaitTermination() {
        Mockito.when(mockCompletableFuture.isDone()).thenReturn(true);
        futures.add(CompletableFuture.runAsync(() -> {
            int invocation = 0;
            while (mockCompletableFuture.isDone()) {
                if (++invocation == 100) {
                    Mockito.when(mockCompletableFuture.isDone()).thenReturn(false);
                }
            }
        }));

        executor.awaitTermination();

        Mockito.verify(mockCompletableFuture, Mockito.times(101)).isDone();
    }

    @Test
    public void testClose() {
        try (SubjectExecutor executor = this.executor) {
            // do something
        }

        Mockito.verify(mockExecutorService).shutdown();
    }

}