package me.aloic.lazybot.component;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.chain.CommandChainProcessor;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.command.registry.LazybotSlashCommandRegistry;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.CommandMonitor;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.rosupp.RosuPpException;
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
    private static final Logger logger = LoggerFactory.getLogger(SlashCommandProcessor.class);
    @Resource
    private LazybotSlashCommandRegistry registry;
    @Resource
    private CommandMonitor commandMonitor;
    @Resource
    private CommandChainProcessor commandChainProcessor;

    @Async("virtualThreadExecutor")
    public void processDiscord(SlashCommandInteractionEvent event)
    {
        processCommand(new CommandExecution(
                event.getName(),
                "Discord",
                event.getUser().getId(),
                event.getChannel().getId(),
                command -> command.execute(event),
                (throwable, expected) -> replyDiscord(
                        event,
                        expected
                                ? safeMessage(throwable)
                                : "出现预期外的错误，请稍后重试")));
    }

    // we r using shiro internal thread executor
    public void processQQ(Bot bot, LazybotSlashCommandEvent event)
    {
        processCommand(new CommandExecution(
                event.getCommandType(),
                "OneBot",
                String.valueOf(event.getMessageEvent().getSender().getUserId()),
                String.valueOf(event.getMessageEvent().getGroupId()),
                command -> commandChainProcessor.process(bot, event, command),
                (throwable, expected) -> replyQQ(
                        bot,
                        event,
                        expected ? safeMessage(throwable)
                                : "出现预期外的错误，请稍后重试")));
    }

    @Async("virtualThreadExecutor")
    public CompletableFuture<Void> processTest(LazybotSlashCommandEvent event)
    {
        processCommand(new CommandExecution(
                event.getCommandType(),
                "TEST",
                "TEST",
                "TEST",
                command -> commandChainProcessor.process(event, command),
                (throwable, expected) -> {
                    // The shared processor already records the classified error.
                }));
        return CompletableFuture.completedFuture(null);
    }

    private void processCommand(CommandExecution execution)
    {
        try {
            LazybotSlashCommand command = registry.getCommand(execution.commandName());
            if (command == null) {
                return;
            }

            logger.info("正在处理 {} 指令({})", execution.commandName(), execution.source());
            commandMonitor.record(execution.commandName(), execution.userId(), execution.channelId());
            execution.invocation().invoke(command);
        }
        catch (Exception exception) {
            Throwable rootCause = unwrap(exception);
            boolean expected = isExpected(rootCause);
            if (expected) {
                logger.warn("{} 指令({})执行失败: {}", execution.commandName(), execution.source(), safeMessage(rootCause));
            }
            else {
                logger.error("{} 指令({})发生预期外异常", execution.commandName(), execution.source(), rootCause);
            }

            try {
                execution.failureHandler().handle(rootCause, expected);
            }
            catch (Exception handlerException) {
                logger.error(
                        "{} 指令({})发送错误响应失败",
                        execution.commandName(),
                        execution.source(),
                        handlerException);
            }
        }
    }

    private static Throwable unwrap(Throwable throwable)
    {
        Throwable current = throwable;
        while ((current instanceof ExecutionException || current instanceof CompletionException)
                && current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private static boolean isExpected(Throwable throwable)
    {
        return throwable instanceof LazybotRuntimeException
                || throwable instanceof IllegalArgumentException
                || throwable instanceof RosuPpException;
    }

    private static String safeMessage(Throwable throwable)
    {
        String message = throwable.getMessage();
        return message == null || message.isBlank()
                ? throwable.getClass().getSimpleName()
                : message;
    }

    private static void replyQQ(
            Bot bot, LazybotSlashCommandEvent event, String message)
    {
        bot.sendGroupMsg(
                event.getMessageEvent().getGroupId(),
                MsgUtils.builder()
                        .text("[Lazybot] " + message)
                        .build(),
                false);
    }

    private static void replyDiscord(
            SlashCommandInteractionEvent event, String message)
    {
        if (event.isAcknowledged()) {
            event.getHook().sendMessage(message).setEphemeral(true).queue();
        }
        else {
            event.reply(message).setEphemeral(true).queue();
        }
    }

    @FunctionalInterface
    private interface CommandInvocation
    {
        void invoke(LazybotSlashCommand command) throws Exception;
    }

    @FunctionalInterface
    private interface FailureHandler
    {
        void handle(Throwable throwable, boolean expected);
    }

    private record CommandExecution(
            String commandName,
            String source,
            String userId,
            String channelId,
            CommandInvocation invocation,
            FailureHandler failureHandler) { }
}
