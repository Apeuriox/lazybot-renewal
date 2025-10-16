package me.aloic.lazybot.component;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.chain.CommandChainProcessor;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.command.registry.LazybotSlashCommandRegistry;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.CommandMonitor;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class SlashCommandProcessor
{
    @Resource
    private LazybotSlashCommandRegistry registry;
    @Resource
    private CommandMonitor commandMonitor;
    @Resource
    private CommandChainProcessor commandChainProcessor;

    private static final Logger logger = LoggerFactory.getLogger(SlashCommandProcessor.class);

    @Async("virtualThreadExecutor")
    public void processDiscord(SlashCommandInteractionEvent event)
    {
        try {
            LazybotSlashCommand command = registry.getCommand(event.getName());
            if (command != null) {
                logger.info("正在处理 {} 命令",event.getName());
                commandMonitor.record(event.getName(),
                        event.getUser().getId(),
                        event.getChannel().getId());
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
                commandMonitor.record(event.getCommandType(),
                        String.valueOf(event.getMessageEvent().getSender().getUserId()),
                        String.valueOf(event.getMessageEvent().getGroupId()));
                commandChainProcessor.process(bot, event, command);
            }
        } catch (LazybotRuntimeException | IllegalArgumentException e) {
            logger.error(e.getMessage());
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("[Lazybot] " + e.getMessage()).build(), false);
        }
        catch (ExecutionException e) {
            Throwable rootCause = e.getCause();
            if (rootCause instanceof LazybotRuntimeException) {
                logger.error(e.getMessage());
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(
                        e.getMessage().replaceFirst("^.*?:\\s*", "")
                ).build(), false);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("[Lazybot] 出现未知错误").build(), false);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async("virtualThreadExecutor")
    public CompletableFuture<Void> processTest(LazybotSlashCommandEvent event) {
            try {
                LazybotSlashCommand command = registry.getCommand(event.getCommandType());
                if (command != null) {
                    logger.info("正在处理 {} 命令(TEST CASE)", event.getCommandType());
                    commandMonitor.record(event.getCommandType(),
                            "TEST",
                            "TEST");
                    commandChainProcessor.process(event, command);
                }
            } catch (LazybotRuntimeException | IllegalArgumentException e)  {
                e.printStackTrace();
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
