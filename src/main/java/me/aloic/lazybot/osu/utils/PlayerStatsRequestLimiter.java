package me.aloic.lazybot.osu.utils;

import java.util.concurrent.TimeUnit;

public final class PlayerStatsRequestLimiter
{
    private final long intervalNanos;
    private long nextAllowedNanos;

    public PlayerStatsRequestLimiter(int permitsPerMinute)
    {
        int permits = Math.max(1, permitsPerMinute);
        this.intervalNanos = TimeUnit.MINUTES.toNanos(1) / permits;
        this.nextAllowedNanos = System.nanoTime();
    }

    public void acquire() throws InterruptedException
    {
        long waitNanos;
        synchronized (this) {
            long now = System.nanoTime();
            if (now < nextAllowedNanos) {
                waitNanos = nextAllowedNanos - now;
                nextAllowedNanos += intervalNanos;
            }
            else {
                waitNanos = 0L;
                nextAllowedNanos = now + intervalNanos;
            }
        }
        if (waitNanos > 0L) {
            TimeUnit.NANOSECONDS.sleep(waitNanos);
        }
    }
}
