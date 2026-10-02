package me.aloic.lazybot.graphics.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import lombok.Getter;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BitmapRenderCache
{
    private static final Logger log = LoggerFactory.getLogger(BitmapRenderCache.class);

    @Getter
    private final boolean enabled;
    private final Cache<String, byte[]> cache;
    private final ConcurrentHashMap<String, CompletableFuture<byte[]>> inflight = new ConcurrentHashMap<>();

    public BitmapRenderCache(@Value("${lazybot.render-cache.enabled:true}") boolean enabled,
                             @Value("${lazybot.render-cache.max-weight-mb:256}") long maxWeightMb,
                             @Value("${lazybot.render-cache.expire-minutes:30}") long expireMinutes)
    {
        boolean actuallyEnabled = enabled && maxWeightMb > 0;
        this.enabled = actuallyEnabled;
        if (actuallyEnabled)
        {
            long cappedMb = Math.clamp(maxWeightMb, 1, 1800);
            long expire = Math.max(expireMinutes, 1);
            this.cache = Caffeine.newBuilder()
                    .maximumWeight(cappedMb * 1024 * 1024)
                    .weigher((Weigher<String, byte[]>) (key, value) -> Math.max(1, value.length))
                    .expireAfterAccess(Duration.ofMinutes(expire))
                    .build();
            log.info("[Render Cache] Render cache enabled: {}MB, expire after {}min idle", cappedMb, expire);
        }
        else
        {
            this.cache = null;
            log.info("[Render Cache] Render cache disabled");
        }
        RendererDistributor.bindCache(this);
    }

    public byte[] getOrCompute(String key, Loader loader)
    {
        if (!enabled) return invoke(loader);

        byte[] hit = cache.getIfPresent(key);
        if (hit != null) {
            log.info("[Render Cache] Render cache hit: {} ({} bytes)", describe(key), hit.length);
            return hit;
        }

        CompletableFuture<byte[]> created = new CompletableFuture<>();
        CompletableFuture<byte[]> winner = inflight.putIfAbsent(key, created);
        if (winner != null) {
            try {
                byte[] joined = winner.join();
                log.info("[Render Cache] Render cache joined: {} ({} bytes)", describe(key), joined == null ? 0 : joined.length);
                return joined;
            }
            catch (CompletionException e) {
                throw unwrapToCertainException(e);
            }
        }

        try
        {
            byte[] value = invoke(loader);
            if (value != null && value.length > 0)
                cache.put(key, value);
            created.complete(value);
            return value;
        }
        catch (RuntimeException e)
        {
            created.completeExceptionally(e);
            throw e;
        }
        finally
        {
            inflight.remove(key, created);
        }
    }

    private static byte[] invoke(Loader loader)
    {
        try {
            return loader.render();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new LazybotRuntimeException(e);
        }
    }

    private static RuntimeException unwrapToCertainException(Throwable throwable)
    {
        Throwable current = throwable;
        if (current instanceof CompletionException && current.getCause() != null)
            current = current.getCause();
        if (current instanceof RuntimeException runtime)
            return runtime;
        if (current instanceof Error error)
            throw error;
        return new LazybotRuntimeException(current);
    }

    private static String describe(String key)
    {
        int first = key.indexOf(':');
        if (first < 0)
            return key;
        int second = key.indexOf(':', first + 1);
        return second > 0 ? key.substring(0, second) : key;
    }

    @FunctionalInterface
    public interface Loader
    {
        byte[] render() throws Exception;
    }
}
