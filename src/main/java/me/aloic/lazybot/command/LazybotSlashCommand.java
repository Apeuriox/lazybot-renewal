package me.aloic.lazybot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface LazybotSlashCommand
{
    void executeDiscord(SlashCommandInteractionEvent event) throws Exception;
}
