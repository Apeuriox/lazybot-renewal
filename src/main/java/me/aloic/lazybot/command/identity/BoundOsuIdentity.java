package me.aloic.lazybot.command.identity;

import java.util.Objects;

/** Safe, platform-neutral view of a user's osu! binding. OAuth tokens are intentionally excluded. */
public record BoundOsuIdentity(
        Integer lazybotUserId,
        Integer playerId,
        String playerName,
        String defaultMode,
        Integer preferredPanelVersion
) {
    public BoundOsuIdentity {
        Objects.requireNonNull(playerId, "playerId");
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("playerName must not be blank");
        }
        if (defaultMode == null || defaultMode.isBlank()) {
            throw new IllegalArgumentException("defaultMode must not be blank");
        }
    }
}
