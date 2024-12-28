package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.parameter.RecentCommandParameter;
import me.aloic.lazybot.parameter.ScoreCommandParameter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface PlayerService
{

    byte[] score(ScoreCommandParameter params) throws Exception;

    byte[] recent(RecentCommandParameter params, Integer type);
}
