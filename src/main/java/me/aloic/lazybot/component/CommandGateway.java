package me.aloic.lazybot.component;

import me.aloic.lazybot.chain.CommandChainProcessor;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.command.core.CommandContext;
import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.ParsedCommand;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;
import me.aloic.lazybot.command.core.TextCommandParser;
import me.aloic.lazybot.command.registry.LazybotSlashCommandRegistry;
import me.aloic.lazybot.command.registry.PlatformIndependentCommandRegistry;
import me.aloic.lazybot.command.pipeline.CommandExecutionPipeline;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.CommandMonitor;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Migration-time application entry point. Platform adapters move here incrementally. */
@Component
public class CommandGateway {
    private final TextCommandParser parser;
    private final PlatformIndependentCommandRegistry independentRegistry;
    private final LazybotSlashCommandRegistry legacyRegistry;
    private final CommandChainProcessor legacyChainProcessor;
    private final CommandExecutionPipeline executionPipeline;
    private final CommandMonitor commandMonitor;
    private final String commandPrefix;

    public CommandGateway(
            TextCommandParser parser,
            PlatformIndependentCommandRegistry independentRegistry,
            LazybotSlashCommandRegistry legacyRegistry,
            CommandChainProcessor legacyChainProcessor,
            CommandExecutionPipeline executionPipeline,
            CommandMonitor commandMonitor,
            @Value("${lazybot.prefix}") String commandPrefix
    ) {
        this.parser = parser;
        this.independentRegistry = independentRegistry;
        this.legacyRegistry = legacyRegistry;
        this.legacyChainProcessor = legacyChainProcessor;
        this.executionPipeline = executionPipeline;
        this.commandMonitor = commandMonitor;
        this.commandPrefix = commandPrefix;
    }

    public CommandResult execute(CommandContext context, String rawCommand) throws Exception {
        ParsedCommand parsedCommand = parser.parse(rawCommand, commandPrefix);
        return execute(new CommandRequest(context, parsedCommand));
    }

    public CommandResult execute(CommandRequest request) throws Exception {
        PlatformIndependentCommand command = independentRegistry.getCommand(request.commandName());
        if (command != null) {
            return executionPipeline.execute(request, command);
        }
        return executeLegacy(request);
    }

    public boolean supportsIndependentCommand(String commandName) {
        return independentRegistry.getCommand(commandName) != null;
    }

    private CommandResult executeLegacy(CommandRequest request) throws Exception {
        LazybotSlashCommand command = legacyRegistry.getCommand(request.commandName());
        if (command == null) {
            throw new LazybotRuntimeException("找不到对应指令: " + request.commandName());
        }

        commandMonitor.record(
                request.commandName(),
                request.context().userId(),
                request.context().channelId()
        );

        ParsedCommand parsed = request.command();
        LazybotSlashCommandEvent legacyEvent = new LazybotSlashCommandEvent(parsed.rawCommand());
        legacyEvent.setIstSlashCommand(true);
        legacyEvent.setCommandType(parsed.name());
        legacyEvent.setCommandParameters(parsed.arguments().positional());
        legacyEvent.setScorePanelVersion(parsed.scorePanelVersion());
        legacyEvent.setOsuMode(parsed.osuMode());
        legacyEvent.setCommandContext(request.context());
        legacyChainProcessor.process(legacyEvent, command);
        return new CommandResult.LegacySideEffect("命令仍使用旧输出通道，结果已写入本地测试目录");
    }
}
