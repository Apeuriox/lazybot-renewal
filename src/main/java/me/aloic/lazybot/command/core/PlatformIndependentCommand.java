package me.aloic.lazybot.command.core;

/** New command contract. Commands can implement this alongside the legacy contract while migrating. */
public interface PlatformIndependentCommand {
    CommandDefinition definition();

    CommandResult execute(CommandRequest request) throws Exception;

    default String getHelp() {
        return "[Lazybot] 暂无帮助文档";
    }

    default String permissionKey() {
        return getClass().getSimpleName();
    }
}
