package me.aloic.lazybot.tencent.command;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import me.aloic.lazybot.command.registry.LazybotSlashCommandRegistry;
import me.aloic.lazybot.osu.enums.IdentityPlatform;
import me.aloic.lazybot.tencent.api.TencentOpenApiClient;
import me.aloic.lazybot.tencent.event.TencentIncomingMessage;
import me.aloic.lazybot.tencent.event.TencentScene;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.shiro.utils.MessageDeduplicator;
import me.aloic.lazybot.shiro.utils.MessageEventFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TencentCommandDispatcher
{
    private static final Logger logger = LoggerFactory.getLogger(TencentCommandDispatcher.class);

    @Resource
    private MessageEventFactory messageEventFactory;
    @Resource
    private MessageDeduplicator messageDeduplicator;
    @Resource
    private TencentOpenApiClient apiClient;
    @Resource
    private LazybotSlashCommandRegistry commandRegistry;

    public void dispatchTencentEvent(JSONObject payload)
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
            logger.warn("Tencent消息缺少openid无法鉴定身份，已忽略");
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
            logger.warn("解析 Tencent 指令失败: {}", e.getMessage());
            return;
        }
        if (!Boolean.TRUE.equals(event.getIstSlashCommand())) {
            return;
        }

        if (commandRegistry.getCommand(event.getCommandType()) == null) {
            return;
        }

        event.setIdentityPlatform(IdentityPlatform.TENCENT);
        event.setPlatformUserId(incoming.getUserOpenid());
        event.setPlatformChannelId(incoming.isGroup() ? incoming.getGroupOpenid() : null);
        event.setSourceMessageId(incoming.getMessageId());
        event.setReply(new TencentReplyChannel(apiClient, incoming));
        event.setCommandString(content);
        messageDeduplicator.replicateCheck(event);
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
