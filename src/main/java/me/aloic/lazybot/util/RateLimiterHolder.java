package me.aloic.lazybot.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RateLimiterHolder {
    private static final int REQUESTS_PER_SECOND = 10;
    private static final Semaphore SEMAPHORE = new Semaphore(REQUESTS_PER_SECOND);

    static {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            int permitsToAdd = REQUESTS_PER_SECOND - SEMAPHORE.availablePermits();
            if (permitsToAdd > 0) {
                SEMAPHORE.release(permitsToAdd);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public static void acquire() {
        try {
            SEMAPHORE.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}