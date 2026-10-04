package me.aloic.lazybot.command;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface LazybotSlashCommand
{
    void execute(SlashCommandInteractionEvent event) throws Exception;
    void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception;
    void execute(LazybotSlashCommandEvent event) throws Exception;

    default CommandHelp commandHelp() {
        return null;
    }

    default String getHelp() {
        CommandHelp help = commandHelp();
        if (help == null) {
            return "[Lazybot] 暂无帮助文档";
        }
        return HelpFormatter.format(help);
    }
}
