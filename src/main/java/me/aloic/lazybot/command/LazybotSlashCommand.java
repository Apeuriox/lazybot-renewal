package me.aloic.lazybot.command;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface LazybotSlashCommand
{
    void execute(SlashCommandInteractionEvent event) throws Exception;
    void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception;
    void execute(LazybotSlashCommandEvent event) throws Exception;

    /**
     * Tencent OpenAPI (group @ / C2C). Default path reuses the OneBot execute
     * when the event has a reply channel. Commands that still touch Bot or
     * GroupMessageEvent stay silent.
     */
    default void execute(CommandReply reply, LazybotSlashCommandEvent event) throws Exception
    {
        if (event.getReply() == null) {
            event.setReply(reply);
        }
        try {
            execute((Bot) null, event);
        }
        catch (NullPointerException ignored) {
        }
    }

    default String getHelp() {
        return "[Lazybot 暂无帮助文档";
    }
}
