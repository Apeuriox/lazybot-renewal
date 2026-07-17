package me.aloic.lazybot.discord;

import me.aloic.lazybot.command.core.CommandArguments;
import me.aloic.lazybot.command.core.CommandContext;
import me.aloic.lazybot.command.core.CommandModifiers;
import me.aloic.lazybot.command.core.CommandPlatform;
import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.ParsedCommand;
import me.aloic.lazybot.osu.enums.OsuMode;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/** Converts typed Discord slash-command options directly into the internal request model. */
@Component
public class DiscordCommandRequestFactory {
    public CommandRequest create(SlashCommandInteractionEvent event) {
        Map<String, Object> namedArguments = new LinkedHashMap<>();
        for (OptionMapping option : event.getOptions()) {
            namedArguments.put(option.getName(), optionValue(option));
        }

        CommandArguments arguments = CommandArguments.named(namedArguments);
        OsuMode osuMode = arguments.string("mode").map(OsuMode::getMode).orElse(null);
        int panelVersion = arguments.integer("version").orElse(0);
        String commandName = event.getName().toLowerCase(Locale.ROOT);
        ParsedCommand parsed = new ParsedCommand(
                commandName,
                arguments,
                new CommandModifiers(osuMode, panelVersion),
                "/" + commandName
        );
        CommandContext context = new CommandContext(
                CommandPlatform.DISCORD,
                event.getUser().getId(),
                event.getChannel().getId(),
                UUID.randomUUID().toString()
        );
        return new CommandRequest(context, parsed);
    }

    private static Object optionValue(OptionMapping option) {
        OptionType type = option.getType();
        return switch (type) {
            case INTEGER -> option.getAsLong();
            case STRING -> option.getAsString();
            default -> option.getAsString();
        };
    }
}
