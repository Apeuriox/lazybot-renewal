package me.aloic.lazybot.command.core;

import me.aloic.lazybot.osu.enums.OsuMode;

/** Cross-command modifiers parsed from text syntax or structured platform options. */
public record CommandModifiers(OsuMode osuMode, int scorePanelVersion) {
    public static CommandModifiers none() {
        return new CommandModifiers(null, 0);
    }
}
