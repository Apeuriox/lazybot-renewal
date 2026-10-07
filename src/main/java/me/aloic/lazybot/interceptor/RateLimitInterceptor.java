package me.aloic.lazybot.interceptor;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor
{

    private final Boolean enabled;
    private final Integer capacity;
    private final Integer refill;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public RateLimitInterceptor(@Value("${rate-limit.enabled}") Boolean enabled,
                                @Value("${rate-limit.capacity}") Integer capacity,
                                @Value("${rate-limit.refill}") Integer refill)
    {
        this.enabled = enabled;
        this.capacity = capacity;
        this.refill = refill;
    }

    private Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, k -> Bucket.builder()
                .addLimit(limit -> limit.capacity(capacity).refillGreedy(refill, Duration.ofMinutes(1)))
                .build());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException
    {
        if (!enabled){
            return true;
        }
        String ip = request.getRemoteAddr();
        Bucket bucket = resolveBucket(ip);
        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":429,\"msg\":\"oops, too many requests\",\"data\":null}");
            return false;
        }
    }
}