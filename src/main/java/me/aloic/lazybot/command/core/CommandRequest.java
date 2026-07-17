package me.aloic.lazybot.command.core;

import java.util.Objects;

/** A fully parsed command ready for the application command bus. */
public record CommandRequest(CommandContext context, ParsedCommand command) {
    public CommandRequest {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(command, "command");
    }

    public String commandName() {
        return command.name();
    }

    public CommandArguments arguments() {
        return command.arguments();
    }

    public java.util.List<String> positionalArguments() {
        return command.arguments().positional();
    }
}
