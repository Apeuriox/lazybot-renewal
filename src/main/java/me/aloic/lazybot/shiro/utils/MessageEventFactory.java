package me.aloic.lazybot.shiro.utils;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import me.aloic.lazybot.command.core.CommandContext;
import me.aloic.lazybot.command.core.CommandPlatform;
import me.aloic.lazybot.command.core.ParsedCommand;
import me.aloic.lazybot.command.core.TextCommandParser;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Adapts OneBot events to the shared text-command parser. */
@Component
public class MessageEventFactory {
    private static final Logger logger = LoggerFactory.getLogger(MessageEventFactory.class);

    private final TextCommandParser parser;
    private final String commandPrefix;

    public MessageEventFactory(TextCommandParser parser, @Value("${lazybot.prefix}") String commandPrefix) {
        this.parser = parser;
        this.commandPrefix = commandPrefix;
    }

    public LazybotSlashCommandEvent setupSlashCommandEvent(GroupMessageEvent event) {
        LazybotSlashCommandEvent slashCommandEvent = new LazybotSlashCommandEvent(event);
        try {
            if (event.getMessage().startsWith(commandPrefix)) {
                slashCommandEvent.setIstSlashCommand(true);
                applyParsedCommand(slashCommandEvent, parser.parse(event.getMessage(), commandPrefix));
                slashCommandEvent.setCommandContext(new CommandContext(
                        CommandPlatform.QQ,
                        String.valueOf(event.getSender().getUserId()),
                        String.valueOf(event.getGroupId()),
                        UUID.randomUUID().toString()
                ));
            }
            return slashCommandEvent;
        }
        catch (Exception e) {
            logger.error("解析参数时出错", e);
            throw new LazybotRuntimeException("解析参数时出错");
        }
    }

    /** Legacy local-test adapter. New callers should invoke CommandGateway directly. */
    public LazybotSlashCommandEvent setupSlashCommandEvent(String command) {
        LazybotSlashCommandEvent slashCommandEvent = new LazybotSlashCommandEvent(command);
        try {
            applyParsedCommand(slashCommandEvent, parser.parse(command, commandPrefix));
            return slashCommandEvent;
        }
        catch (Exception e) {
            logger.error("[TEST]解析参数时出错", e);
            throw new LazybotRuntimeException("[TEST]解析参数时出错");
        }
    }

    private static void applyParsedCommand(LazybotSlashCommandEvent event, ParsedCommand parsed) {
        event.setCommandType(parsed.name());
        event.setCommandParameters(parsed.arguments().positional());
        event.setScorePanelVersion(parsed.scorePanelVersion());
        event.setOsuMode(parsed.osuMode());
    }
}
