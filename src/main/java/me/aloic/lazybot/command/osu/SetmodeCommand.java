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

@LazybotCommandMapping({"setmode"})
@Component
public class SetmodeCommand implements LazybotSlashCommand
{
    @Resource
    private UserService userService;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        userService.updateDefaultMode(event);
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        userService.updateDefaultMode(bot, event);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        //not implemented
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Set Default Mode","Setmode",
                        "更改默认模式",
                        "Aloic", null, "2023-06-29")
                        .addExample("/Setmode 1")
                        .addOption(new CommandParameter("Mode","指定的模式", CommandParameter.ParameterType.MUST)));
    }
}
