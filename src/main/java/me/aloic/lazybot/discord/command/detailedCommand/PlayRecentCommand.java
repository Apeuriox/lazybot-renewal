package me.aloic.lazybot.discord.command.detailedCommand;

import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.discord.command.LazybotSlashCommand;
import me.aloic.lazybot.osu.service.PlayerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping("pr")
@Component
public class PlayRecentCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        playerService.recent(event,1);
    }
}
