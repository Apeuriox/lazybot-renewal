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

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        //do not implement
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Link","Link",
                        "用户绑定",
                        "Aloic", null, "2023-04-04")
                        .addExample("/Link Aloic")
                        .addExample("/Link oauth")
                        .addOption(new CommandParameter(
                                "PlayerName / oauth",
                                "输入用户名为手动绑定；输入 oauth 验证本人身份",
                                CommandParameter.ParameterType.MUST)));
    }
}
