package me.aloic.lazybot.command.core;

import java.util.Objects;

public record CommandOptionDefinition(
        CommandOptionType type,
        String name,
        String description,
        boolean required,
        boolean autoComplete
) {
    public CommandOptionDefinition {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(description, "description");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Command option name must not be blank");
        }
    }

    public static CommandOptionDefinition string(String name, String description, boolean required) {
        return new CommandOptionDefinition(CommandOptionType.STRING, name, description, required, false);
    }

    public static CommandOptionDefinition integer(String name, String description, boolean required) {
        return new CommandOptionDefinition(CommandOptionType.INTEGER, name, description, required, false);
    }
}
