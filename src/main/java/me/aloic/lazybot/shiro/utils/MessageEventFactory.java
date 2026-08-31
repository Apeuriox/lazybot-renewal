package me.aloic.lazybot.shiro.utils;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import me.aloic.lazybot.command.registry.LazybotSlashCommandRegistry;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Component
public class MessageEventFactory
{
    private static final Logger logger = LoggerFactory.getLogger(MessageEventFactory.class);
    private static final Map<String, OsuMode> MODE_SUFFIXES = Map.of(
            ":0", OsuMode.Osu,
            ":1", OsuMode.Taiko,
            ":2", OsuMode.Catch,
            ":3", OsuMode.Mania);

    private final LazybotSlashCommandRegistry commandRegistry;
    private final String commandPrefix;

    public MessageEventFactory(LazybotSlashCommandRegistry commandRegistry,
                               @Value("${lazybot.prefix}") String commandPrefix)
    {
        this.commandRegistry = commandRegistry;
        this.commandPrefix = Objects.requireNonNull(commandPrefix, "commandPrefix");
    }

    public LazybotSlashCommandEvent setupSlashCommandEvent(GroupMessageEvent event) {
        LazybotSlashCommandEvent slashCommandEvent = new LazybotSlashCommandEvent(event);
        try {
            String message = convertString(event.getMessage());
            if (!isCommandCandidate(message)) {
                return slashCommandEvent;
            }

            slashCommandEvent.setIstSlashCommand(analyzeCommand(slashCommandEvent, message, true));
            return slashCommandEvent;
        }
        catch (Exception e) {
            logger.error("解析参数时出错", e);
            throw new LazybotRuntimeException("解析参数时出错", e);
        }
    }

    public LazybotSlashCommandEvent setupSlashCommandEvent(String command)
    {
        LazybotSlashCommandEvent slashCommandEvent = new LazybotSlashCommandEvent(command);
        try {
            analyzeCommand(slashCommandEvent, command, false);
            return slashCommandEvent;
        }
        catch (IllegalArgumentException iae) {
            return null;
        }
        catch (Exception e) {
            logger.error("[TEST]解析参数时出错", e);
            throw new LazybotRuntimeException("[TEST]解析参数时出错", e);
        }
    }

    /**
     * Parse a raw chat string (Tencent group @ / C2C) using the same
     * prefix, mode suffix and panel-version rules as OneBot.
     */
    public LazybotSlashCommandEvent parseTextCommand(String command)
    {
        LazybotSlashCommandEvent slashCommandEvent = new LazybotSlashCommandEvent();
        slashCommandEvent.setScorePanelVersion(1);
        try {
            String message = convertString(command);
            if (!isCommandCandidate(message)) {
                slashCommandEvent.setIstSlashCommand(false);
                return slashCommandEvent;
            }
            slashCommandEvent.setIstSlashCommand(analyzeCommand(slashCommandEvent, message, true));
            return slashCommandEvent;
        }
        catch (IllegalArgumentException iae) {
            slashCommandEvent.setIstSlashCommand(false);
            return slashCommandEvent;
        }
        catch (Exception e) {
            logger.error("解析 Tencent 指令时出错", e);
            throw new LazybotRuntimeException("解析参数时出错", e);
        }
    }

    private boolean analyzeCommand(LazybotSlashCommandEvent event,
                                   String command,
                                   boolean requireRegisteredCommand) {
        String body = commandBody(convertString(command));
        List<String> initialTokens = tokenize(body);
        String commandName = initialTokens.getFirst().toLowerCase(Locale.ROOT);
        if (requireRegisteredCommand
                && commandRegistry.getCommand(commandName) == null) {
            return false;
        }

        List<String> finalTokens;
        if (commandRegistry.shouldSkipPreprocessing(commandName)) {
            finalTokens = initialTokens;
        }
        else {
            finalTokens = preprocess(event, body);
        }

        event.setCommandType(finalTokens.getFirst().toLowerCase(Locale.ROOT));
        event.setCommandParameters(finalTokens.subList(1, finalTokens.size()));
        return true;
    }

    private static List<String> preprocess(LazybotSlashCommandEvent event, String commandBody)
    {
        List<String> rawTokens = tokenize(commandBody);
        extractAtParameters(event, rawTokens);

        String formatted = formatCommand(String.join(" ", rawTokens));
        event.setScorePanelVersion(countOccurrences(formatted, '&'));
        formatted = formatted.replace("&", "");

        List<String> tokens = tokenize(formatted);
        for (int index = 1; index < tokens.size(); index++) {
            String token = tokens.get(index);
            if (!token.startsWith(":")) {
                continue;
            }

            OsuMode mode = MODE_SUFFIXES.get(token);
            if (mode != null) {
                event.setOsuMode(mode);
            }
            tokens.remove(index);
            break;
        }
        return tokens;
    }

    // mostly used for target algorithm recalculation. might be useful for future expansion
    private static void extractAtParameters(LazybotSlashCommandEvent event, List<String> tokens)
    {
        List<String> values = new ArrayList<>();
        for (int index = 1; index < tokens.size(); index++) {
            String token = tokens.get(index);
            if (!token.startsWith("@")) {
                continue;
            }
            values.add(token.substring(1));
            tokens.remove(index--);
        }
        event.setAtParameters(List.copyOf(values));
    }

    private String commandBody(String command)
    {
        Objects.requireNonNull(command, "command");
        if (!command.startsWith(commandPrefix)) {
            throw new IllegalArgumentException("指令必须以 " + commandPrefix + " 开头");
        }

        String body = command.substring(commandPrefix.length()).stripLeading();
        if (body.isBlank()) {
            throw new IllegalArgumentException("指令内容不能为空");
        }
        return body;
    }

    private boolean isCommandCandidate(String message)
    {
        return message != null
                && message.startsWith(commandPrefix)
                && !message.substring(commandPrefix.length()).isBlank();
    }

    private static List<String> tokenize(String command)
    {
        return new ArrayList<>(List.of(command.trim().split("\\s+")));
    }

    private static String formatCommand(String command)
    {
        return command
                .replace('！', '!')
                .replace('：', ':')
                .trim()
                .replaceAll("(?<!\\s)([:&])", " $1")
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    public static String convertString(String input)
    {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input
                .replaceAll("[\u200B\u200C\u200D\uFEFF]", "")
                .replace("&#91;", "[")
                .replace("&#93;", "]")
                .replace("&amp;", "&")
                .replace("&#44;", ",");
    }

    public static int countOccurrences(String originalStr, char target)
    {
        int count = 0;
        for (char current : originalStr.toCharArray()) {
            if (current == target) {
                count++;
            }
        }
        return count;
    }
}
