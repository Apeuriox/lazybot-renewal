package me.aloic.lazybot.command.detailedCommand;

import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.osu.service.DiscordUserService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"unlink"})
@Component
public class UnLinkCommand implements LazybotSlashCommand
{
    @Resource
    private DiscordUserService userService;

    @Override
    public void executeDiscord(SlashCommandInteractionEvent event) throws Exception {
        userService.unlinkUser(event);
    }
}
