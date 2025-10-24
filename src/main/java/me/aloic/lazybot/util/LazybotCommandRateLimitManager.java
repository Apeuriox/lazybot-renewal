package me.aloic.lazybot.util;

import io.github.bucket4j.*;
import me.aloic.lazybot.annotation.LazybotRateLimit;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LazybotCommandRateLimitManager
{
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key, LazybotRateLimit config) {
        return buckets.computeIfAbsent(key, k -> createBucket(config));
    }

    private Bucket createBucket(LazybotRateLimit config) {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(config.capacity())
                        .refillIntervally(config.refillTokens(), Duration.of(config.refillPeriod(), config.unit().toChronoUnit()))
                )
                .build();
    }

    public boolean tryConsume(String key, LazybotRateLimit config) {
        Bucket bucket = resolveBucket(key, config);
        return bucket.tryConsume(1);
    }

    public long getRemainingTokens(String key, LazybotRateLimit config) {
        return resolveBucket(key, config).getAvailableTokens();
    }
}
