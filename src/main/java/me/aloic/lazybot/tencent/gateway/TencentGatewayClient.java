package me.aloic.lazybot.tencent.gateway;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import jakarta.annotation.PreDestroy;
import me.aloic.lazybot.osu.monitor.TokenMonitor;
import me.aloic.lazybot.tencent.api.TencentOpenApiClient;
import me.aloic.lazybot.tencent.command.TencentCommandDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class TencentGatewayClient
{
    private static final Logger logger = LoggerFactory.getLogger(TencentGatewayClient.class);
    private static final int OP_DISPATCH = 0;
    private static final int OP_HEARTBEAT = 1;
    private static final int OP_IDENTIFY = 2;
    private static final int OP_RESUME = 6;
    private static final int OP_RECONNECT = 7;
    private static final int OP_INVALID_SESSION = 9;
    private static final int OP_HELLO = 10;
    private static final int OP_HEARTBEAT_ACK = 11;
    private static final long HELLO_TIMEOUT_MS = 20_000L;
    private static final long AUTH_TIMEOUT_MS = 20_000L;
    private static final long SEND_TIMEOUT_SECONDS = 8L;
    private static final long MAX_BACKOFF_MS = 60_000L;
    private static final long SESSION_WINDOW_MS = 5_000L;

    private final TencentOpenApiClient apiClient;
    private final TokenMonitor tokenMonitor;
    private final TencentCommandDispatcher dispatcher;
    private final int intents;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean sessionReady = new AtomicBoolean(false);
    private final AtomicReference<WebSocket> socket = new AtomicReference<>();
    private final AtomicReference<String> sessionId = new AtomicReference<>();
    private final AtomicReference<Integer> lastSequence = new AtomicReference<>();
    private final AtomicBoolean heartbeatAcked = new AtomicBoolean(true);
    private final Object sendLock = new Object();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "tencent-gateway");
        thread.setDaemon(true);
        return thread;
    });
    private volatile ScheduledFuture<?> heartbeatFuture;
    private volatile ScheduledFuture<?> helloTimeoutFuture;
    private volatile ScheduledFuture<?> authTimeoutFuture;
    private volatile Listener activeListener;
    private volatile long heartbeatIntervalMs = 45_000L;
    private volatile long lastInboundNanos = System.nanoTime();
    private volatile long lastIdentifyAtNanos;
    private volatile Thread loopThread;

    public TencentGatewayClient(
            TencentOpenApiClient apiClient,
            TokenMonitor tokenMonitor,
            TencentCommandDispatcher dispatcher,
            @Value("${tencent.bot.intents:33554432}") int intents)
    {
        this.apiClient = apiClient;
        this.tokenMonitor = tokenMonitor;
        this.dispatcher = dispatcher;
        this.intents = intents;
    }

    public void start()
    {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        loopThread = Thread.ofVirtual().name("tencent-ws-loop").start(this::runLoop);
    }

    @PreDestroy
    public void stop()
    {
        running.set(false);
        closeCurrent("shutdown");
        scheduler.shutdownNow();
        if (loopThread != null) {
            loopThread.interrupt();
        }
    }

    private void runLoop()
    {
        long backoffMs = 1000L;
        int attempt = 0;
        while (running.get()) {
            attempt++;
            CountDownLatch closed = new CountDownLatch(1);
            Listener listener = new Listener(closed);
            sessionReady.set(false);
            activeListener = listener;
            try {
                TencentOpenApiClient.GatewayEndpoint endpoint = apiClient.getGateway();
                boolean resume = canResume();
                if (!resume) {
                    endpoint = waitForSessionQuota(endpoint);
                }
                String authorization = "QQBot " + TokenMonitor.getTencentToken();
                logger.info(
                        "正在连接 Tencent Gateway (第 {} 次, {}, shards={}, remaining={}): {}",
                        attempt,
                        resume ? "Resume" : "Identify",
                        endpoint.shards(),
                        endpoint.remaining(),
                        endpoint.url());
                httpClient.newWebSocketBuilder()
                        .connectTimeout(Duration.ofSeconds(20))
                        .header("Authorization", authorization)
                        .header("X-Union-Appid", tokenMonitor.getTencentAppId())
                        .header("User-Agent", "lazybot")
                        .buildAsync(URI.create(endpoint.url()), listener)
                        .orTimeout(30, TimeUnit.SECONDS)
                        .join();
                closed.await();
                if (sessionReady.get()) {
                    backoffMs = 1000L;
                    attempt = 0;
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (!running.get()) {
                    break;
                }
                logger.warn("Tencent Gateway 等待被中断，将重连");
            }
            catch (Exception e) {
                logger.error("Tencent Gateway 连接失败: {}", e.getMessage(), e);
            }
            finally {
                listener.finish();
                cancelHeartbeat();
                cancelHelloTimeout();
                cancelAuthTimeout();
                WebSocket leftover = socket.getAndSet(null);
                if (leftover != null) {
                    try {
                        leftover.abort();
                    }
                    catch (Exception ignored) {
                    }
                }
                if (activeListener == listener) {
                    activeListener = null;
                }
            }
            if (!running.get()) {
                break;
            }
            try {
                logger.info("{} ms 后重连 Tencent Gateway", backoffMs);
                TimeUnit.MILLISECONDS.sleep(backoffMs);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (!running.get()) {
                    break;
                }
            }
            backoffMs = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
        }
        logger.info("Tencent Gateway 重连循环已结束");
    }

    private void handlePayload(JSONObject payload)
    {
        Integer op = payload.getInteger("op");
        if (op == null) {
            return;
        }
        lastInboundNanos = System.nanoTime();
        Integer sequence = payload.getInteger("s");
        if (sequence != null) {
            lastSequence.set(sequence);
        }
        switch (op) {
            case OP_HELLO -> onHello(payload.getJSONObject("d"));
            case OP_DISPATCH -> onDispatch(payload);
            case OP_HEARTBEAT_ACK -> {
                heartbeatAcked.set(true);
                logger.trace("收到 Tencent Gateway 心跳 ACK");
            }
            case OP_RECONNECT -> {
                logger.warn("Tencent Gateway 要求重新连接");
                closeCurrent("server-reconnect");
            }
            case OP_INVALID_SESSION -> {
                boolean resumable = Boolean.TRUE.equals(payload.getBoolean("d"));
                logger.warn("Tencent Gateway session 无效 (opcode 9), resumable={}", resumable);
                if (!resumable) {
                    clearResumeState();
                }
                closeCurrent("invalid-session");
            }
            case OP_HEARTBEAT -> sendHeartbeat();
            default -> logger.info("未处理的 Gateway opcode {} payload={}", op, payload);
        }
    }

    private void onHello(JSONObject data)
    {
        cancelHelloTimeout();
        heartbeatIntervalMs = data == null ? 45000L : Math.max(5000L, data.getLongValue("heartbeat_interval", 45000L));
        logger.info("收到 Hello，heartbeat_interval={} ms", heartbeatIntervalMs);
        armAuthTimeout();
        if (canResume()) {
            sendResume();
        }
        else {
            sendIdentify();
        }
    }

    private void onDispatch(JSONObject payload)
    {
        String type = payload.getString("t");
        logger.trace("收到 Dispatch 事件 {}", type);
        if ("READY".equals(type)) {
            JSONObject data = payload.getJSONObject("d");
            if (data != null) {
                sessionId.set(data.getString("session_id"));
                JSONObject user = data.getJSONObject("user");
                logger.info(
                        "Tencent机器人已就绪: {} session={}",
                        user == null ? "?" : user.getString("username"),
                        sessionId.get());
            }
            markSessionReady();
            startHeartbeat(heartbeatIntervalMs);
            return;
        }
        if ("RESUMED".equals(type)) {
            logger.info("Tencent Gateway session 已恢复");
            markSessionReady();
            startHeartbeat(heartbeatIntervalMs);
            return;
        }
        try {
            dispatcher.dispatchTencentEvent(payload);
        }
        catch (Exception e) {
            logger.error("处理 Tencent事件 {} 失败", type, e);
        }
    }

    private void markSessionReady()
    {
        cancelAuthTimeout();
        sessionReady.set(true);
        heartbeatAcked.set(true);
        lastInboundNanos = System.nanoTime();
    }

    private void sendIdentify()
    {
        JSONObject properties = new JSONObject();
        properties.put("$os", "java");
        properties.put("$browser", "lazybot");
        properties.put("$device", "lazybot");
        JSONObject data = new JSONObject();
        data.put("token", "QQBot " + TokenMonitor.getTencentToken());
        data.put("intents", intents);
        data.put("shard", new int[]{0, 1});
        data.put("properties", properties);
        JSONObject payload = new JSONObject();
        payload.put("op", OP_IDENTIFY);
        payload.put("d", data);
        lastIdentifyAtNanos = System.nanoTime();
        sendJson(payload);
        logger.info("已发送 Identify，intents={}", intents);
    }

    private void sendResume()
    {
        JSONObject data = new JSONObject();
        Integer seq = lastSequence.get();
        data.put("token", "QQBot " + TokenMonitor.getTencentToken());
        data.put("session_id", sessionId.get());
        data.put("seq", seq == null ? 0 : seq);
        JSONObject payload = new JSONObject();
        payload.put("op", OP_RESUME);
        payload.put("d", data);
        sendJson(payload);
        logger.info("已发送 Resume, session={}, seq={}", sessionId.get(), seq == null ? 0 : seq);
    }

    private void startHeartbeat(long intervalMs)
    {
        cancelHeartbeat();
        heartbeatIntervalMs = Math.max(5000L, intervalMs);
        heartbeatAcked.set(true);
        lastInboundNanos = System.nanoTime();
        Thread.ofVirtual().name("tencent-heartbeat").start(this::sendHeartbeat);
        heartbeatFuture = scheduler.scheduleAtFixedRate(
                this::heartbeatTick,
                heartbeatIntervalMs,
                heartbeatIntervalMs,
                TimeUnit.MILLISECONDS);
        logger.info("Tencent Gateway 心跳间隔 {} ms（鉴权成功后按 Hello 周期发送）", heartbeatIntervalMs);
    }

    private void heartbeatTick()
    {
        if (!running.get() || socket.get() == null) {
            return;
        }
        long silentMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - lastInboundNanos);
        if (silentMs > heartbeatIntervalMs * 2) {
            logger.warn("Tencent Gateway 已 {} ms 未收到下行，准备重连", silentMs);
            closeCurrent("inbound-timeout");
            return;
        }
        if (!heartbeatAcked.get()) {
            logger.warn("Tencent Gateway 心跳未确认，准备重连");
            closeCurrent("heartbeat-timeout");
            return;
        }
        Thread.ofVirtual().name("tencent-heartbeat").start(this::sendHeartbeat);
    }

    private void sendHeartbeat()
    {
        heartbeatAcked.set(false);
        JSONObject payload = new JSONObject();
        payload.put("op", OP_HEARTBEAT);
        payload.put("d", lastSequence.get());
        logger.trace("发送 Tencent Gateway 心跳, seq={}", lastSequence.get());
        sendJson(payload);
    }

    private void sendJson(JSONObject payload)
    {
        String json = payload.toJSONString(JSONWriter.Feature.WriteMapNullValue);
        synchronized (sendLock) {
            WebSocket socketToUse = socket.get();
            if (socketToUse == null) {
                logger.warn("Gateway 尚未就绪，丢弃发送: op={}", payload.getInteger("op"));
                return;
            }
            try {
                socketToUse.sendText(json, true)
                        .orTimeout(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .join();
            }
            catch (Exception e) {
                logger.warn("向 Gateway 发送数据失败: {}", e.getMessage());
                closeCurrent("send-failed");
            }
        }
    }

    private void closeCurrent(String reason)
    {
        logger.info("关闭当前 Tencent Gateway 连接: {}", reason);
        cancelHeartbeat();
        cancelHelloTimeout();
        cancelAuthTimeout();
        WebSocket current = socket.getAndSet(null);
        if (current != null) {
            try {
                current.abort();
            }
            catch (Exception ignored) {
            }
        }
        Listener listener = activeListener;
        if (listener != null) {
            listener.finish();
        }
    }

    private void applyCloseCode(int statusCode)
    {
        if (statusCode == 4914 || statusCode == 4915) {
            logger.error("Tencent 机器人不可连接 (关闭码 {})，停止重连", statusCode);
            running.set(false);
            clearResumeState();
            return;
        }
        if (statusCode == 4008 || statusCode == 4009) {
            logger.warn("关闭码 {}，将 Resume 重连", statusCode);
            return;
        }
        if (statusCode < 4000) {
            return;
        }
        logger.warn("关闭码 {}，将重新 Identify", statusCode);
        clearResumeState();
    }

    private boolean canResume()
    {
        String id = sessionId.get();
        return id != null && !id.isBlank();
    }

    private TencentOpenApiClient.GatewayEndpoint waitForSessionQuota(TencentOpenApiClient.GatewayEndpoint endpoint)
            throws InterruptedException
    {
        while (running.get() && endpoint.remaining() <= 0) {
            long waitMs = Math.max(1000L, endpoint.resetAfterMs());
            logger.warn(
                    "Tencent session_start_limit.remaining=0，{} ms 后重新获取接入点",
                    waitMs);
            TimeUnit.MILLISECONDS.sleep(waitMs);
            endpoint = apiClient.getGateway();
        }
        long minGapMs = Math.max(1000L, SESSION_WINDOW_MS / endpoint.maxConcurrency());
        if (lastIdentifyAtNanos > 0) {
            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - lastIdentifyAtNanos);
            if (elapsedMs < minGapMs) {
                long waitMs = minGapMs - elapsedMs;
                logger.info(
                        "遵守 session_start_limit.max_concurrency={}，等待 {} ms 后再 Identify",
                        endpoint.maxConcurrency(),
                        waitMs);
                TimeUnit.MILLISECONDS.sleep(waitMs);
            }
        }
        return endpoint;
    }

    private void clearResumeState()
    {
        sessionId.set(null);
        lastSequence.set(null);
    }

    private void armHelloTimeout()
    {
        cancelHelloTimeout();
        helloTimeoutFuture = scheduler.schedule(
                () -> {
                    logger.warn("连接后 {} ms 内未收到 Hello，准备重连", HELLO_TIMEOUT_MS);
                    closeCurrent("hello-timeout");
                },
                HELLO_TIMEOUT_MS,
                TimeUnit.MILLISECONDS);
    }

    private void cancelHeartbeat()
    {
        ScheduledFuture<?> future = heartbeatFuture;
        if (future != null) {
            future.cancel(false);
            heartbeatFuture = null;
        }
    }

    private void cancelHelloTimeout()
    {
        ScheduledFuture<?> future = helloTimeoutFuture;
        if (future != null) {
            future.cancel(false);
            helloTimeoutFuture = null;
        }
    }

    private void armAuthTimeout()
    {
        cancelAuthTimeout();
        authTimeoutFuture = scheduler.schedule(
                () -> {
                    logger.warn("Identify/Resume 后 {} ms 内未就绪，准备重连", AUTH_TIMEOUT_MS);
                    closeCurrent("auth-timeout");
                },
                AUTH_TIMEOUT_MS,
                TimeUnit.MILLISECONDS);
    }

    private void cancelAuthTimeout()
    {
        ScheduledFuture<?> future = authTimeoutFuture;
        if (future != null) {
            future.cancel(false);
            authTimeoutFuture = null;
        }
    }

    private final class Listener implements WebSocket.Listener
    {
        private final CountDownLatch closed;
        private final AtomicBoolean finished = new AtomicBoolean(false);
        private final StringBuilder textBuffer = new StringBuilder();

        private Listener(CountDownLatch closed)
        {
            this.closed = closed;
        }

        private void finish()
        {
            if (finished.compareAndSet(false, true)) {
                closed.countDown();
            }
        }

        @Override
        public void onOpen(WebSocket webSocket)
        {
            if (finished.get()) {
                try {
                    webSocket.abort();
                }
                catch (Exception ignored) {
                }
                return;
            }
            socket.set(webSocket);
            lastInboundNanos = System.nanoTime();
            logger.info("Tencent Gateway WebSocket 已打开");
            armHelloTimeout();
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last)
        {
            textBuffer.append(data);
            if (last) {
                String raw = textBuffer.toString();
                textBuffer.setLength(0);
                logger.trace("Tencent Gateway 收到: {}", raw);
                Thread.ofVirtual().name("tencent-ws-in").start(() -> {
                    try {
                        handlePayload(JSON.parseObject(raw));
                    }
                    catch (Exception e) {
                        logger.warn("解析 Gateway 消息失败: {}", e.getMessage());
                    }
                });
            }
            webSocket.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last)
        {
            webSocket.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message)
        {
            lastInboundNanos = System.nanoTime();
            webSocket.sendPong(message);
            webSocket.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason)
        {
            logger.warn("Tencent Gateway 已关闭: {} {}", statusCode, reason);
            applyCloseCode(statusCode);
            finish();
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error)
        {
            logger.error("Tencent Gateway 出错", error);
            finish();
        }
    }
}
