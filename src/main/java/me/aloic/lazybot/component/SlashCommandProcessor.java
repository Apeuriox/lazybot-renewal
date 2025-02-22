package me.aloic.lazybot.component;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.command.registry.LazybotSlashCommandRegistry;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class SlashCommandProcessor
{
    @Resource
    private LazybotSlashCommandRegistry registry;

    private static final Logger logger = LoggerFactory.getLogger(SlashCommandProcessor.class);

    @Async("asyncServiceExecutor")
    public void processDiscord(SlashCommandInteractionEvent event)
    {
        try {
            LazybotSlashCommand command = registry.getCommand(event.getName());
            if (command != null) {
                logger.info("正在处理 {} 命令",event.getName());
                command.execute(event);
            } else {
                event.reply("找不到对应指令").setEphemeral(true).queue();
            }
        } catch (LazybotRuntimeException e) {
            logger.error(e.getMessage());
            ErrorResultHandler.createExceptionMessage(event, e);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    @Async("asyncServiceExecutor")
    public CompletableFuture<Void> processQQ(Bot bot, LazybotSlashCommandEvent event) {
        return CompletableFuture.runAsync(() -> {
            try {
                LazybotSlashCommand command = registry.getCommand(event.getCommandType());
                if (command != null) {
                    logger.info("正在处理 {} 命令(Onebot)", event.getCommandType());
                    command.execute(bot, event);
                }
            } catch (LazybotRuntimeException e) {
                logger.error(e.getMessage());
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(e.getMessage()).build(), false);
            } catch (Exception e) {
                logger.error(e.getMessage());
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("出现未知错误").build(), false);
            }
        });
    }

}
