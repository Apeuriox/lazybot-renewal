package me.aloic.lazybot.shiro.utils;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.component.SlashCommandProcessor;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/*
*   这玩意用来去重消息
*   因为一个群可能会有多个Bot连接本项目
*   不搞去重就会重复执行
*/

@Component
public class MessageDeduplicator
{

    @Resource
    private SlashCommandProcessor slashCommandProcessor;

    private static final ConcurrentHashMap<String, Boolean> messageRecords;
    private static final Logger logger = LoggerFactory.getLogger(MessageDeduplicator.class);
    static{
        messageRecords = new ConcurrentHashMap<>();
    }

    public void replicateCheck(Bot bot, LazybotSlashCommandEvent event) {
        if (event.getIstSlashCommand()) {
            String key = event.getMessageEvent().getSender().getUserId() + ":"
                    + event.getMessageEvent().getGroupId() + ":"
                    + event.getMessageEvent().getMessage();
            if (messageRecords.putIfAbsent(key, true) != null) {
                logger.info("记录 {} 已存在，已跳过执行", key);
                return;
            }
            logger.info("正在处理记录: {}", key);
            try {
                CompletableFuture<Void> future = slashCommandProcessor.processQQ(bot, event);
                future.whenComplete((result, throwable) -> {
                    messageRecords.remove(key);
                    logger.info("已移除记录: {}", key);
                    if (throwable != null) {
                        logger.error("处理命令时发生异常: {}", throwable.getMessage(), throwable);
                    }
                });
            } catch (Exception e) {
                messageRecords.remove(key);
                logger.error("处理命令时发生异常: {}", e.getMessage(), e);
            }
        }
    }
}
