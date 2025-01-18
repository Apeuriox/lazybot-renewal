package me.aloic.lazybot.command;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface LazybotSlashCommand
{
    void execute(SlashCommandInteractionEvent event) throws Exception;
    void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception;
}
