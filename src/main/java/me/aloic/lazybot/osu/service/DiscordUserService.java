package me.aloic.lazybot.osu.service;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

@Service
public interface DiscordUserService
{
    void linkUser(SlashCommandInteractionEvent event);

    void unlinkUser(SlashCommandInteractionEvent event);
}
