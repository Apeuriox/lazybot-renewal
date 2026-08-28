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

    private final TencentOpenApiClient apiClient;
    private final TokenMonitor tokenMonitor;
    private final TencentCommandDispatcher dispatcher;
    private final int intents;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    private final AtomicBoolean running = new AtomicBoolean(false);
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
        cancelHeartbeat();
        WebSocket current = socket.getAndSet(null);
        if (current != null) {
            try {
                current.sendClose(WebSocket.NORMAL_CLOSURE, "shutdown");
            }
            catch (Exception ignored) {
            }
        }
        scheduler.shutdownNow();
        if (loopThread != null) {
            loopThread.interrupt();
        }
    }

    private void runLoop()
    {
        long backoffMs = 1000L;
        while (running.get()) {
            CountDownLatch closed = new CountDownLatch(1);
            try {
                String url = apiClient.getGatewayUrl();
                String authorization = "QQBot " + TokenMonitor.getTencentToken();
                logger.info("正在连接 Tencent Gateway: {}", url);
                Listener listener = new Listener(closed);
                httpClient.newWebSocketBuilder()
                        .connectTimeout(Duration.ofSeconds(20))
                        .header("Authorization", authorization)
                        .header("X-Union-Appid", tokenMonitor.getTencentAppId())
                        .header("User-Agent", "lazybot")
                        .buildAsync(URI.create(url), listener)
                        .join();
                closed.await();
                backoffMs = 1000L;
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            catch (Exception e) {
                logger.error("Tencent Gateway 连接失败: {}", e.getMessage(), e);
            }
            finally {
                cancelHeartbeat();
                socket.set(null);
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
                break;
            }
            backoffMs = Math.min(backoffMs * 2, 60_000L);
        }
    }

    private void handlePayload(JSONObject payload)
    {
        Integer op = payload.getInteger("op");
        if (op == null) {
            return;
        }
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
                closeCurrent("reconnect");
            }
            case OP_INVALID_SESSION -> {
                logger.warn("Tencent Gateway session 无效，将重新 Identify");
                sessionId.set(null);
                lastSequence.set(null);
                closeCurrent("invalid-session");
            }
            case OP_HEARTBEAT -> sendHeartbeat();
            default -> logger.info("未处理的 Gateway opcode {} payload={}", op, payload);
        }
    }

    private void onHello(JSONObject data)
    {
        long interval = data == null ? 45000L : data.getLongValue("heartbeat_interval", 45000L);
        logger.info("收到 Hello，heartbeat_interval={} ms", interval);
        startHeartbeat(interval);
        if (sessionId.get() != null && lastSequence.get() != null) {
            sendResume();
        }
        else {
            sendIdentify();
        }
    }

    private void onDispatch(JSONObject payload)
    {
        String type = payload.getString("t");
        logger.info("收到 Dispatch 事件 {}", type);
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
            heartbeatAcked.set(true);
            sendHeartbeat();
            return;
        }
        if ("RESUMED".equals(type)) {
            logger.info("Tencent Gateway session 已恢复");
            heartbeatAcked.set(true);
            sendHeartbeat();
            return;
        }
        try {
            dispatcher.dispatch(payload);
        }
        catch (Exception e) {
            logger.error("处理 Tencent事件 {} 失败", type, e);
        }
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
        sendJson(payload);
        logger.info("已发送 Identify，intents={}", intents);
    }

    private void sendResume()
    {
        JSONObject data = new JSONObject();
        data.put("token", "QQBot " + TokenMonitor.getTencentToken());
        data.put("session_id", sessionId.get());
        data.put("seq", lastSequence.get());
        JSONObject payload = new JSONObject();
        payload.put("op", OP_RESUME);
        payload.put("d", data);
        sendJson(payload);
        logger.info("已发送 Resume, seq={}", lastSequence.get());
    }

    private void startHeartbeat(long intervalMs)
    {
        cancelHeartbeat();
        heartbeatAcked.set(true);
        long delayMs = Math.max(1000L, (long) (intervalMs * 0.8d));
        heartbeatFuture = scheduler.scheduleWithFixedDelay(
                this::beatOrReconnect,
                delayMs,
                delayMs,
                TimeUnit.MILLISECONDS);
        logger.info("Tencent Gateway 心跳间隔 {} ms（按服务端 {} ms 的 80%）", delayMs, intervalMs);
    }

    private void beatOrReconnect()
    {
        if (!heartbeatAcked.get()) {
            logger.warn("Tencent Gateway 心跳未确认，准备重连");
            closeCurrent("heartbeat-timeout");
            return;
        }
        sendHeartbeat();
    }

    private void sendHeartbeat()
    {
        heartbeatAcked.set(false);
        JSONObject payload = new JSONObject();
        payload.put("op", OP_HEARTBEAT);
        payload.put("d", lastSequence.get());
        logger.info("发送 Tencent Gateway 心跳, seq={}", lastSequence.get());
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
                socketToUse.sendText(json, true).join();
            }
            catch (Exception e) {
                logger.warn("向 Gateway 发送数据失败: {}", e.getMessage());
                closeCurrent("send-failed");
            }
        }
    }

    private void closeCurrent(String reason)
    {
        WebSocket current = socket.getAndSet(null);
        if (current != null) {
            try {
                current.sendClose(WebSocket.NORMAL_CLOSURE, reason);
            }
            catch (Exception ignored) {
            }
            try {
                current.abort();
            }
            catch (Exception ignored) {
            }
        }
    }

    private void cancelHeartbeat()
    {
        ScheduledFuture<?> future = heartbeatFuture;
        if (future != null) {
            future.cancel(true);
            heartbeatFuture = null;
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
            socket.set(webSocket);
            logger.info("Tencent Gateway WebSocket 已打开");
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
            webSocket.sendPong(message);
            webSocket.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason)
        {
            logger.warn("Tencent Gateway 已关闭: {} {}", statusCode, reason);
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
