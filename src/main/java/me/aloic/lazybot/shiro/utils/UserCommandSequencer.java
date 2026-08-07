package me.aloic.lazybot.shiro.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Executes commands from the same QQ user in FIFO order while allowing
 * different users to run concurrently.
 */
@Component
public class UserCommandSequencer
{
    private static final Logger logger = LoggerFactory.getLogger(UserCommandSequencer.class);

    private final ConcurrentHashMap<Long, UserQueue> queues = new ConcurrentHashMap<>();
    private final Executor executor;
    private final int maxPendingPerUser;

    public UserCommandSequencer(@Qualifier("virtualThreadExecutor") Executor executor,
                                @Value("${lazybot.command.max-pending-per-user:16}") int maxPendingPerUser)
    {
        this.executor = executor;
        this.maxPendingPerUser = Math.max(1, maxPendingPerUser);
    }

    /**
     * @return the command completion, or empty when THIS user's queue is full
     */
    public Optional<CompletableFuture<Void>> submit(long userId, Runnable command)
    {
        Objects.requireNonNull(command, "command should not be null");
        AtomicReference<UserQueue> selectedQueue = new AtomicReference<>();
        AtomicReference<CompletableFuture<Void>> selectedFuture = new AtomicReference<>();
        queues.compute(userId, (ignored, queue) -> {
            UserQueue current = queue == null ? new UserQueue() : queue;
            if (current.pending >= maxPendingPerUser) {
                return current;
            }

            CompletableFuture<Void> next = current.tail
                    .handle((result, throwable) -> null)
                    .thenRunAsync(command, executor);
            current.tail = next;
            current.pending++;
            selectedQueue.set(current);
            selectedFuture.set(next);
            return current;
        });

        CompletableFuture<Void> future = selectedFuture.get();
        if (future == null) {
            return Optional.empty();
        }

        UserQueue queue = selectedQueue.get();
        future.whenComplete((ignored, throwable) -> {
            if (throwable != null) {
                logger.error(
                        "用户命令执行发生未捕获异常: userId={}",
                        userId,
                        throwable);
            }
            queues.computeIfPresent(userId, (ignoredKey, current) -> {
                if (current != queue) {
                    return current;
                }
                current.pending--;
                return current.pending == 0 && current.tail == future
                        ? null
                        : current;
            });
        });

        return Optional.of(future);
    }

    private static final class UserQueue
    {
        private CompletableFuture<Void> tail =
                CompletableFuture.completedFuture(null);
        private int pending;
    }
}
