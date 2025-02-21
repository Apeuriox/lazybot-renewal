package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.osu.service.UserService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"link"})
@Component
public class LinkCommand implements LazybotSlashCommand
{
    @Resource
    private UserService userService;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        userService.linkUser(event);
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        userService.linkUser(bot, event);
    }
}
