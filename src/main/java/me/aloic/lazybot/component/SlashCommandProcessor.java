package me.aloic.lazybot.component;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.chain.CommandChainProcessor;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.command.core.CommandContext;
import me.aloic.lazybot.command.core.CommandArguments;
import me.aloic.lazybot.command.core.CommandModifiers;
import me.aloic.lazybot.command.core.CommandPlatform;
import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.ParsedCommand;
import me.aloic.lazybot.command.registry.LazybotSlashCommandRegistry;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.DiscordCommandRequestFactory;
import me.aloic.lazybot.discord.DiscordCommandResultDispatcher;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.CommandMonitor;
import me.aloic.lazybot.shiro.handler.OnebotCommandResultDispatcher;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.UUID;

@Component
public class SlashCommandProcessor
{
    @Resource
    private LazybotSlashCommandRegistry registry;
    @Resource
    private CommandMonitor commandMonitor;
    @Resource
    private CommandChainProcessor commandChainProcessor;
    @Resource
    private CommandGateway commandGateway;
    @Resource
    private OnebotCommandResultDispatcher onebotResultDispatcher;
    @Resource
    private DiscordCommandRequestFactory discordRequestFactory;
    @Resource
    private DiscordCommandResultDispatcher discordResultDispatcher;

    private static final Logger logger = LoggerFactory.getLogger(SlashCommandProcessor.class);

    @Async("virtualThreadExecutor")
    public void processDiscord(SlashCommandInteractionEvent event)
    {
        try {
            if (commandGateway.supportsIndependentCommand(event.getName())) {
                logger.info("正在处理 {} 命令",event.getName());
                event.deferReply().queue();
                CommandRequest request = discordRequestFactory.create(event);
                discordResultDispatcher.dispatch(event, commandGateway.execute(request));
                return;
            }

            LazybotSlashCommand command = registry.getCommand(event.getName());
            if (command == null) {
                event.reply("找不到对应指令").setEphemeral(true).queue();
                return;
            }
            logger.info("正在处理 {} 命令",event.getName());
            commandMonitor.record(event.getName(), event.getUser().getId(), event.getChannel().getId());
            command.execute(event);
        } catch (LazybotRuntimeException e) {
            logger.error(e.getMessage());
            ErrorResultHandler.createExceptionMessage(event, e);
        }
        catch (Exception e) {
            logger.error("Discord命令执行失败", e);
            if (event.isAcknowledged()) {
                event.getHook().sendMessage("出现预期外的错误").setEphemeral(true).queue();
            }
            else {
                event.reply("出现预期外的错误").setEphemeral(true).queue();
            }
        }
    }
    @Async("virtualThreadExecutor")
    public CompletableFuture<Void> processQQ(Bot bot, LazybotSlashCommandEvent event) {
        try {
            logger.info("正在处理 {} 命令(Onebot)", event.getCommandType());
            if (commandGateway.supportsIndependentCommand(event.getCommandType())) {
                CommandRequest request = createIndependentRequest(event);
                onebotResultDispatcher.dispatch(bot, event, commandGateway.execute(request));
            }
            else {
                LazybotSlashCommand command = registry.getCommand(event.getCommandType());
                if (command != null) {
                    commandMonitor.record(event.getCommandType(),
                            String.valueOf(event.getMessageEvent().getSender().getUserId()),
                            String.valueOf(event.getMessageEvent().getGroupId()));
                    commandChainProcessor.process(bot, event, command);
                }
            }
        } catch (LazybotRuntimeException | IllegalArgumentException e) {
            logger.error(e.getMessage());
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("[Lazybot] " + e.getMessage()).build(), false);
        }
        catch (ExecutionException e) {
            Throwable rootCause = e.getCause();
            if (rootCause instanceof LazybotRuntimeException) {
                logger.error(rootCause.getMessage());
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(
                        "[Lazybot] " + rootCause.getMessage()
                ).build(), false);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("[Lazybot] 出现预期外的错误: " + e.getMessage()).build(), false);
        }
        return CompletableFuture.completedFuture(null);
    }

    private CommandRequest createIndependentRequest(LazybotSlashCommandEvent event) {
        CommandContext context = event.getCommandContext();
        if (context == null) {
            context = new CommandContext(
                    CommandPlatform.QQ,
                    String.valueOf(event.getMessageEvent().getSender().getUserId()),
                    String.valueOf(event.getMessageEvent().getGroupId()),
                    UUID.randomUUID().toString()
            );
        }
        ParsedCommand parsed = new ParsedCommand(
                event.getCommandType(),
                CommandArguments.positional(
                        event.getCommandParameters() == null ? java.util.List.of() : event.getCommandParameters()
                ),
                new CommandModifiers(
                        event.getOsuMode(),
                        event.getScorePanelVersion() == null ? 0 : event.getScorePanelVersion()
                ),
                event.getMessageEvent().getMessage()
        );
        return new CommandRequest(context, parsed);
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
                logger.error("捕获到预期内exception: {}", e.getMessage(),e);
            }
            catch (ExecutionException e) {
                Throwable rootCause = e.getCause();
                if (rootCause instanceof LazybotRuntimeException) {
                    logger.error("捕获到多线程处理中的预期内exception: {}", rootCause.getMessage(), rootCause);
                }
            }
            catch (Exception e) {
                logger.error("预期外exception发生: {}",e.getMessage(),e);
            }
        return CompletableFuture.completedFuture(null);
    }

}
