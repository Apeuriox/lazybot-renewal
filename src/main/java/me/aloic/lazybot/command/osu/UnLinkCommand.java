package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.CommandReply;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.service.UserService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"unlink"})
@Component
public class UnLinkCommand implements LazybotSlashCommand
{
    @Resource
    private UserService userService;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        userService.unlinkUser(event);
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        userService.unlinkUser(bot, event);
    }

    @Override
    public void execute(CommandReply reply, LazybotSlashCommandEvent event)
    {
        userService.unlinkUser(reply, event);
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
                new CommandHelp("Cancel Link","Unlink",
                        "解除用户绑定，不要问我为什么Link不能直接覆盖，我就想问你乱绑别人有什么意义",
                        "Aloic", null, "2023-04-04")
                        .addExample("/unlink"));
    }
}
