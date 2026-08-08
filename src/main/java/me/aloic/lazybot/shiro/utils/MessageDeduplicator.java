package me.aloic.lazybot.shiro.utils;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.component.SlashCommandProcessor;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/*
 * 同时处理两种重复消息：
 * 1. 多个 Bot 对同一个 OneBot messageId 的重复上报；
 * 2. 用户在原指令仍在排队或执行时，再次发送相同内容的指令。
 */
@Component
@SuppressWarnings({"unused","StringTemplateMigration"})
//only for Onebot messages, aka Tencent qq platform. you may ask why? cuz Discord have internal message queue somehow.
public class MessageDeduplicator
{
    private static final long IN_FLIGHT = Long.MAX_VALUE;
    private static final Logger logger = LoggerFactory.getLogger(MessageDeduplicator.class);
    //filter same message from multiple bot instance. OneBot message id -> expiry
    private final ConcurrentHashMap<String, Long> deliveryRecords = new ConcurrentHashMap<>();
    //filter same user in certain group spamming same command
    //Entries only exist while the original command is queued or executing
    //key is user + group + actual command content
    private final ConcurrentHashMap<String, Boolean> inFlightCommands = new ConcurrentHashMap<>();
    private final SlashCommandProcessor slashCommandProcessor;
    private final UserCommandSequencer commandSequencer;
    private final long deliveryRetentionNanos;

    public MessageDeduplicator(SlashCommandProcessor slashCommandProcessor,
                               UserCommandSequencer commandSequencer,
                               @Value("${lazybot.command.message-dedup-retention-seconds:30}") long retentionSeconds)
    {
        this.slashCommandProcessor = slashCommandProcessor;
        this.commandSequencer = commandSequencer;
        this.deliveryRetentionNanos =
                TimeUnit.SECONDS.toNanos(Math.max(1, retentionSeconds));
    }

    public void replicateCheck(Bot bot, LazybotSlashCommandEvent event)
    {
        if (!Boolean.TRUE.equals(event.getIstSlashCommand())) {
            return;
        }
        //for multi bot instance returning same message
        String deliveryKey = deliveryKey(event);
        if (deliveryKey != null && !reserveDelivery(deliveryKey)) {
            logger.info("OneBot 消息 {} 已处理，跳过重复上报", deliveryKey);
            return;
        }

        //for same user spamming same command request while the actual one being processing
        String commandKey = commandKey(event);
        if (inFlightCommands.putIfAbsent(commandKey, true) != null) {
            completeDelivery(deliveryKey);
            logger.info("{} 指令正在排队或执行，已合并重复请求", event.getCommandType());
            return;
        }

        logger.info("{} 指令进入用户队列", event.getCommandType());
        try {
            long userId = event.getMessageEvent().getSender().getUserId();
            Optional<CompletableFuture<Void>> submitted = commandSequencer.submit(userId, () -> slashCommandProcessor.processQQ(bot, event));
            if (submitted.isEmpty()) {
                inFlightCommands.remove(commandKey, true);
                removeDelivery(deliveryKey);
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(),
                        MsgUtils.builder()
                                .text("[Lazybot] 当前排队指令过多，请稍后重试")
                                .build(),
                        false);
                return;
            }

            submitted.get().whenComplete((ignored, throwable) -> {
                inFlightCommands.remove(commandKey, true);
                completeDelivery(deliveryKey);
                logger.info("{} 指令执行完成，已释放请求合并记录", event.getCommandType());
            });
        }
        catch (RuntimeException e) {
            inFlightCommands.remove(commandKey, true);
            removeDelivery(deliveryKey);
            logger.error("处理命令时发生异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Scheduled(fixedDelayString = "${lazybot.command.message-dedup-cleanup-ms:60000}")
    public void cleanupExpiredRecords()
    {
        long now = System.nanoTime();
        deliveryRecords.entrySet().removeIf(
                entry -> entry.getValue() != IN_FLIGHT
                        && entry.getValue() - now <= 0);
    }

    private boolean reserveDelivery(String key)
    {
        long now = System.nanoTime();
        //ok i shall add some comments cuz someone cant understand my code if it is kinda abstract.
        //this while true loop below is a lock-free CAS loop, for multithread to read and modified the same object without synchronized
        //for example there are 2 threads.
        //1. Thread A and B read the same old expired timestamp at the same time.
        //2. Thread A modified existing object's value to IN_FLIGHT successfully, returning TRUE.
        //3. Thread B failed to modified, cuz the value was already modified by Thread A.
        //4. Thread B starting the second loop to reread value.
        //5. Thread B read the existing value as IN_FLIGHT, returning FALSE.
        //
        //this works cuz putIfAbsent and replace methods are ATOMIC.
        //
        //some will say it only moves the lock into the ConcurrentHashMap internal methods, it cant cancel it out. Ill answer that later
        while (true) {
            Long existing = deliveryRecords.putIfAbsent(key, IN_FLIGHT);
            if (existing == null) {
                return true;
            }
            if (existing == IN_FLIGHT || existing - now > 0) {
                return false;
            }
            if (deliveryRecords.replace(key, existing, IN_FLIGHT)) {
                return true;
            }
        }
    }

    private void completeDelivery(String key)
    {
        if (key != null) {
            deliveryRecords.replace(key, IN_FLIGHT, System.nanoTime() + deliveryRetentionNanos);
        }
    }

    private void removeDelivery(String key)
    {
        if (key != null) {
            deliveryRecords.remove(key, IN_FLIGHT);
        }
    }

    private static String deliveryKey(LazybotSlashCommandEvent event)
    {
        var message = event.getMessageEvent();
        return message.getMessageId() == null
                ? null
                : message.getGroupId() + ":" + message.getMessageId();
    }

    private static String commandKey(LazybotSlashCommandEvent event)
    {
        var message = event.getMessageEvent();
        return message.getSender().getUserId()
                + ":" + message.getGroupId()
                + ":" + message.getMessage();
    }
}
