package me.aloic.lazybot.command.core;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.enums.OsuMode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Parses Lazybot's platform-independent text command dialect. */
@Component
public class TextCommandParser {
    private static final Map<String, OsuMode> MODE_MAP = Map.of(
            ":0", OsuMode.Osu,
            ":1", OsuMode.Taiko,
            ":2", OsuMode.Catch,
            ":3", OsuMode.Mania
    );

    private static final List<String> IGNORE_PREPROCESS_COMMANDS = List.of(
            "help", "customize", "addtips", "tips", "tns", "tnp", "thumbnail", "bum", "bm", "redeem"
    );

    public ParsedCommand parse(String rawCommand, String prefix) {
        if (rawCommand == null || rawCommand.isBlank()) {
            throw new LazybotRuntimeException("命令不能为空");
        }
        if (prefix == null || prefix.isEmpty() || !rawCommand.startsWith(prefix)) {
            throw new LazybotRuntimeException("命令必须以 " + prefix + " 开头");
        }

        String body = normalize(rawCommand.substring(prefix.length())).trim();
        if (body.isEmpty()) {
            throw new LazybotRuntimeException("命令名不能为空");
        }

        List<String> initialTokens = tokenize(body);
        String initialName = initialTokens.getFirst().toLowerCase(Locale.ROOT);
        if (IGNORE_PREPROCESS_COMMANDS.contains(initialName)) {
            return new ParsedCommand(
                    initialName,
                    CommandArguments.positional(initialTokens.subList(1, initialTokens.size())),
                    CommandModifiers.none(),
                    rawCommand
            );
        }

        int scorePanelVersion = countOccurrences(body, '&');
        String tokenizable = body
                .replaceAll("(?<!\\s):", " :")
                .replace("&", " ");
        List<String> tokens = new ArrayList<>(tokenize(tokenizable));
        OsuMode osuMode = null;
        for (var iterator = tokens.iterator(); iterator.hasNext(); ) {
            String token = iterator.next();
            if (token.startsWith(":")) {
                OsuMode parsedMode = MODE_MAP.get(token.toLowerCase(Locale.ROOT));
                if (parsedMode != null) {
                    osuMode = parsedMode;
                }
                iterator.remove();
            }
        }
        if (tokens.isEmpty()) {
            throw new LazybotRuntimeException("命令名不能为空");
        }

        String name = tokens.getFirst().toLowerCase(Locale.ROOT);
        return new ParsedCommand(
                name,
                CommandArguments.positional(tokens.subList(1, tokens.size())),
                new CommandModifiers(osuMode, scorePanelVersion),
                rawCommand
        );
    }

    private static List<String> tokenize(String value) {
        return new ArrayList<>(List.of(value.trim().split("\\s+")));
    }

    private static String normalize(String input) {
        return input
                .replaceAll("[\\u200B\\u200C\\u200D\\uFEFF]", "")
                .replace("&#91;", "[")
                .replace("&#93;", "]")
                .replace("&amp;", "&")
                .replace("&#44;", ",")
                .replace("！", "!")
                .replace("：", ":");
    }

    private static int countOccurrences(String value, char target) {
        int count = 0;
        for (char current : value.toCharArray()) {
            if (current == target) {
                count++;
            }
        }
        return count;
    }
}
