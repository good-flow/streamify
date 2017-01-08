package com.goodflow.streamify.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class DaemonExecutorService implements ExecutorService {

    private final ExecutorService underlying;
    private final int secondsToAwaitTermination;

    public DaemonExecutorService(ExecutorService executorService) {
        this.underlying = executorService;
        this.secondsToAwaitTermination = 10;
    }

    public DaemonExecutorService(ExecutorService executorService, int secondsToAwaitTermination) {
        this.underlying = executorService;
        this.secondsToAwaitTermination = secondsToAwaitTermination;
    }

    @Override
    public void shutdown() {
        underlying.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return underlying.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return underlying.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return underlying.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return underlying.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return underlying.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return underlying.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return underlying.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return underlying.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return underlying.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return underlying.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return underlying.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        underlying.execute(command);
    }

}
