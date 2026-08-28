package me.aloic.lazybot.tencent.command;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import me.aloic.lazybot.command.registry.LazybotSlashCommandRegistry;
import me.aloic.lazybot.component.SlashCommandProcessor;
import me.aloic.lazybot.osu.enums.IdentityPlatform;
import me.aloic.lazybot.tencent.api.TencentOpenApiClient;
import me.aloic.lazybot.tencent.event.TencentIncomingMessage;
import me.aloic.lazybot.tencent.event.TencentScene;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.shiro.utils.MessageEventFactory;
import me.aloic.lazybot.shiro.utils.UserCommandSequencer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TencentCommandDispatcher
{
    private static final Logger logger = LoggerFactory.getLogger(TencentCommandDispatcher.class);
    private static final String DEFAULT_COMMANDS = "bp,best,pbp,b,card,link,unlink,setmode,help";
    private static final String UNSUPPORTED =
            "[Lazybot] Tencent 通道目前仅支持 /bp /card /link /unlink /setmode /help";

    private final ConcurrentHashMap<String, Long> seenMessageIds = new ConcurrentHashMap<>();
    private final Set<String> allowedCommands;
    private final long deliveryRetentionNanos;

    @Resource
    private MessageEventFactory messageEventFactory;
    @Resource
    private SlashCommandProcessor slashCommandProcessor;
    @Resource
    private UserCommandSequencer commandSequencer;
    @Resource
    private TencentOpenApiClient apiClient;
    @Resource
    private LazybotSlashCommandRegistry commandRegistry;

    public TencentCommandDispatcher(@Value("${tencent.bot.commands:" + DEFAULT_COMMANDS + "}") String commands,
                                    @Value("${lazybot.command.message-dedup-retention-seconds:30}") long retentionSeconds)
    {
        this.allowedCommands = Arrays.stream(commands.split(","))
                .map(value -> value.trim().toLowerCase(Locale.ROOT))
                .filter(value -> !value.isBlank())
                .collect(Collectors.toUnmodifiableSet());
        this.deliveryRetentionNanos = TimeUnit.SECONDS.toNanos(Math.max(1, retentionSeconds));
    }

    public void dispatch(JSONObject payload)
    {
        if (payload == null) {
            return;
        }
        String type = payload.getString("t");
        if (type == null) {
            return;
        }
        String eventId = payload.getString("id");
        JSONObject data = payload.getJSONObject("d");
        switch (type) {
            case "GROUP_AT_MESSAGE_CREATE", "GROUP_MESSAGE_CREATE", "C2C_MESSAGE_CREATE" ->
                    handleIncomingMessage(TencentIncomingMessage.fromDispatch(type, eventId, data));
            case "GROUP_ADD_ROBOT" -> handleWelcome(
                    TencentIncomingMessage.fromDispatch(type, eventId, data),
                    "成功添加 Lazybot");
            case "FRIEND_ADD" -> handleWelcome(
                    TencentIncomingMessage.fromDispatch(type, eventId, data),
                    "cocacola esupma");
            default -> logger.debug("忽略 Tencent事件 {}", type);
        }
    }

    private void handleIncomingMessage(TencentIncomingMessage incoming)
    {
        if (incoming.getUserOpenid() == null || incoming.targetOpenid() == null) {
            logger.warn("Tencent消息缺少 openid，已忽略");
            return;
        }
        if (incoming.getMessageId() != null && !reserveMessage(incoming.getMessageId())) {
            logger.info("Tencent消息 {} 已处理，跳过重复推送", incoming.getMessageId());
            return;
        }
        String content = normalizeContent(incoming.getContent());
        if (content == null || content.isBlank()) {
            return;
        }
        LazybotSlashCommandEvent event;
        try {
            event = messageEventFactory.parseTextCommand(content);
        }
        catch (Exception e) {
            logger.warn("解析 Tencent指令失败: {}", e.getMessage());
            return;
        }
        if (!Boolean.TRUE.equals(event.getIstSlashCommand())) {
            return;
        }

        event.setIdentityPlatform(IdentityPlatform.TENCENT);
        event.setPlatformUserId(incoming.getUserOpenid());
        event.setPlatformChannelId(incoming.isGroup() ? incoming.getGroupOpenid() : null);
        event.setReply(new TencentReplyChannel(apiClient, incoming));
        event.setCommandString(content);

        String commandType = event.getCommandType();
        if (commandRegistry.getCommand(commandType) == null) {
            return;
        }
        if (!allowedCommands.contains(commandType.toLowerCase(Locale.ROOT))) {
            try {
                event.getReply().sendText(UNSUPPORTED);
            }
            catch (Exception e) {
                logger.warn("回复未支持指令失败: {}", e.getMessage());
            }
            return;
        }

        String userKey = "tencent:" + incoming.getUserOpenid();
        Optional<CompletableFuture<Void>> submitted = commandSequencer.submit(
                userKey, () -> slashCommandProcessor.processTencent(event));
        if (submitted.isEmpty()) {
            try {
                event.getReply().sendText("[Lazybot] 当前排队指令过多，请稍后重试");
            }
            catch (Exception e) {
                logger.warn("回复排队已满失败: {}", e.getMessage());
            }
        }
    }

    private void handleWelcome(TencentIncomingMessage incoming, String text)
    {
        if (incoming.getEventId() == null || incoming.targetOpenid() == null) {
            return;
        }
        try {
            apiClient.sendEventText(incoming.getScene() == null ? TencentScene.GROUP : incoming.getScene(),
                    incoming.targetOpenid(),
                    incoming.getEventId(),
                    text);
        }
        catch (Exception e) {
            logger.warn("发送 Tencent欢迎消息失败: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "${lazybot.command.message-dedup-cleanup-ms:60000}")
    public void cleanupExpiredRecords()
    {
        long now = System.nanoTime();
        seenMessageIds.entrySet().removeIf(entry -> entry.getValue() - now <= 0);
    }

    private boolean reserveMessage(String messageId)
    {
        long expiry = System.nanoTime() + deliveryRetentionNanos;
        Long previous = seenMessageIds.putIfAbsent(messageId, expiry);
        if (previous == null) {
            return true;
        }
        if (previous - System.nanoTime() > 0) {
            return false;
        }
        return seenMessageIds.replace(messageId, previous, expiry);
    }

    static String normalizeContent(String content)
    {
        if (content == null) {
            return null;
        }
        return content
                .replace('\u00A0', ' ')
                .replaceAll("[\u200B\u200C\u200D\uFEFF]", "")
                .replaceAll("^\\s*<@!?[^>]+>\\s*", "")
                .strip();
    }
}
