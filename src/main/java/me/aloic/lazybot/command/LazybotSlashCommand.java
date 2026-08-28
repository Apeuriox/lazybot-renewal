package me.aloic.lazybot.command;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface LazybotSlashCommand
{
    void execute(SlashCommandInteractionEvent event) throws Exception;
    void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception;
    void execute(LazybotSlashCommandEvent event) throws Exception;

    /**
     * Tencent OpenAPI (group @ / C2C). Commands that have not been ported
     * keep the default implementation.
     */
    default void execute(CommandReply reply, LazybotSlashCommandEvent event) throws Exception
    {
        throw new LazybotRuntimeException("该指令暂未支持 Tencent 机器人");
    }

    default String getHelp() {
        return "[Lazybot 暂无帮助文档";
    }
}
