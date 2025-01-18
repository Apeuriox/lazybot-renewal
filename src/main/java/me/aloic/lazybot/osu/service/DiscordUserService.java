package me.aloic.lazybot.osu.service;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

@Service
public interface DiscordUserService
{
    void linkUser(SlashCommandInteractionEvent event);

    void linkUser(Bot bot, LazybotSlashCommandEvent event);

    void unlinkUser(SlashCommandInteractionEvent event);

    void unlinkUser(Bot bot, LazybotSlashCommandEvent event);
}
