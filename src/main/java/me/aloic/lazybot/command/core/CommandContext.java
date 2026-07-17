package me.aloic.lazybot.command.core;

import java.util.Objects;
import java.util.UUID;

/** Platform-neutral identity and routing information for one command call. */
public record CommandContext(
        CommandPlatform platform,
        String userId,
        String channelId,
        String requestId
) {
    public CommandContext {
        Objects.requireNonNull(platform, "platform");
        userId = requireText(userId, "userId");
        channelId = requireText(channelId, "channelId");
        requestId = requireText(requestId, "requestId");
    }

    public static CommandContext http(String userId, String channelId) {
        return new CommandContext(CommandPlatform.HTTP_DEV, userId, channelId, UUID.randomUUID().toString());
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
