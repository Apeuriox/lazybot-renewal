package me.aloic.lazybot.command.core;

/** Result of parsing the shared text-command dialect. */
public record ParsedCommand(
        String name,
        CommandArguments arguments,
        CommandModifiers modifiers,
        String rawCommand
) {
    public int scorePanelVersion() {
        return modifiers.scorePanelVersion();
    }

    public me.aloic.lazybot.osu.enums.OsuMode osuMode() {
        return modifiers.osuMode();
    }
}
