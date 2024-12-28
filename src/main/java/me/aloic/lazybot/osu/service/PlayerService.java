package me.aloic.lazybot.osu.service;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface PlayerService
{

    void score(SlashCommandInteractionEvent event) throws Exception;

    void recent(SlashCommandInteractionEvent event, Integer type) throws Exception;
}
