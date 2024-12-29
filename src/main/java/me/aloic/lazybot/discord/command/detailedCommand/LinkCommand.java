package me.aloic.lazybot.discord.command.detailedCommand;

import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.discord.command.LazybotSlashCommand;
import me.aloic.lazybot.osu.service.DiscordUserService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"link"})
@Component
public class LinkCommand implements LazybotSlashCommand
{
    @Resource
    private DiscordUserService userService;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        userService.linkUser(event);
    }
}
