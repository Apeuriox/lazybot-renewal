package me.aloic.lazybot.command.core;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Metadata owned by a platform-independent command. */
public record CommandDefinition(
        String name,
        List<String> aliases,
        String description,
        boolean discordEnabled,
        List<CommandOptionDefinition> options
) {
    public CommandDefinition {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Command name must not be blank");
        }
        aliases = aliases == null ? List.of() : List.copyOf(aliases);
        description = description == null ? "" : description;
        options = options == null ? List.of() : List.copyOf(options);
    }

    public static CommandDefinition internal(String name, List<String> aliases) {
        return new CommandDefinition(name, aliases, "", false, List.of());
    }

    public static CommandDefinition discord(
            String name,
            List<String> aliases,
            String description,
            List<CommandOptionDefinition> options
    ) {
        return new CommandDefinition(name, aliases, description, true, options);
    }

    public Set<String> allNames() {
        Set<String> names = new LinkedHashSet<>();
        names.add(name);
        names.addAll(aliases);
        return Set.copyOf(names);
    }
}
