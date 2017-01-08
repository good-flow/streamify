package com.goodflow.streamify.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DaemonExecutors {

    private DaemonExecutors() {
    }

    public static DaemonExecutorService newFixedThreadPool(int nThreads) {
        return new DaemonExecutorService(Executors.newFixedThreadPool(nThreads, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        }));
    }

}
