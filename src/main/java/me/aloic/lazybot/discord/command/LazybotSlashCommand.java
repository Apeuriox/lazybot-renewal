package me.aloic.lazybot.discord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface LazybotSlashCommand
{
    void execute(SlashCommandInteractionEvent event) throws Exception;
}
