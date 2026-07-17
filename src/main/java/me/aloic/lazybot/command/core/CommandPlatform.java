package me.aloic.lazybot.command.core;

/**
 * The transport that delivered a command. Business commands should use this
 * value only when behaviour genuinely differs by platform.
 */
public enum CommandPlatform {
    QQ,
    DISCORD,
    HTTP_DEV,
    LOCAL_TEST
}
