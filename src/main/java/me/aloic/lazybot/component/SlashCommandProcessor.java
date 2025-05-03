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
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Component
public class SlashCommandProcessor
{
    @Resource
    private LazybotSlashCommandRegistry registry;


    private static final Logger logger = LoggerFactory.getLogger(SlashCommandProcessor.class);

    @Async("virtualThreadExecutor")
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
    @Async("virtualThreadExecutor")
    public CompletableFuture<Void> processQQ(Bot bot, LazybotSlashCommandEvent event) {
        try {
            LazybotSlashCommand command = registry.getCommand(event.getCommandType());
            if (command != null) {
                logger.info("正在处理 {} 命令(Onebot)", event.getCommandType());
                command.execute(bot, event);
            }
        } catch (LazybotRuntimeException | IllegalArgumentException e) {
            logger.error(e.getMessage());
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(e.getMessage()).build(), false);
        }
        catch (ExecutionException e) {
            Throwable rootCause = e.getCause();
            if (rootCause instanceof LazybotRuntimeException) {
                logger.error(e.getMessage());
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(e.getMessage()).build(), false);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("出现未知错误").build(), false);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async("virtualThreadExecutor")
    public CompletableFuture<Void> processTest(LazybotSlashCommandEvent event) {
            try {
                LazybotSlashCommand command = registry.getCommand(event.getCommandType());
                if (command != null) {
                    logger.info("正在处理 {} 命令(TEST CASE)", event.getCommandString());
                    command.execute(event);
                }
            } catch (LazybotRuntimeException | IllegalArgumentException e)  {
                logger.error("捕获到预期内exception: {}", e.getMessage());
            }
            catch (ExecutionException e) {
                Throwable rootCause = e.getCause();
                if (rootCause instanceof LazybotRuntimeException) {
                    logger.error("捕获到多线程处理中的预期内exception: {}", e.getMessage());
                }
            }
            catch (Exception e) {
                logger.error("预期外exception发生: {}",e.getMessage());
                e.printStackTrace();
            }
        return CompletableFuture.completedFuture(null);
    }
}
