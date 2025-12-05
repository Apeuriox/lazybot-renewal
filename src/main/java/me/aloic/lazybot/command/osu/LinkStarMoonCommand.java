package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.service.UserService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"linksm"})
@Component
public class LinkStarMoonCommand implements LazybotSlashCommand
{
    @Resource
    private UserService userService;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {

    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        userService.linkStarMoon(bot, event);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        //do not implement
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Link Star Moon","Linksm",
                        "用户绑定Star Moon",
                        "Aloic", null, "2025-11-12")
                        .addExample("/Linksm Aloic")
                        .addOption(new CommandParameter("PlayerName","指定的用户名称", CommandParameter.ParameterType.MUST)));
    }
}
