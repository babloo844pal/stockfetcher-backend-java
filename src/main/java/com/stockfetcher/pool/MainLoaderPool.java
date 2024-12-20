package com.stockfetcher.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

@Component
public class MainLoaderPool {

    private final ExecutorService executorService;

    public MainLoaderPool() {
        this.executorService = Executors.newFixedThreadPool(5); // Thread pool size
    }

    /**
     * Submit a task to the main loader pool.
     *
     * @param task Runnable task to execute
     */
    public void submitTask(Runnable task) {
        executorService.submit(task);
    }
}
