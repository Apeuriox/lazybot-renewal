package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.enums.OsuSubruleset;
import me.aloic.lazybot.osu.service.UserService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"setruleset","setrule",})
@Component
public class SetRulesetCommand implements LazybotSlashCommand
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
        CommandResultHandler.sendMessageToGroupOnebot(bot, event,
                userService.updateDefaultMode(
                        OsuSubruleset.getRuleset(
                                event.getCommandParameters().getFirst()), event.getMessageEvent().getSender().getUserId()
                )
        );
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
                new CommandHelp("Set Default Subruleset","Setruleset, Setrule",
                        "仅限Star Moon，更改默认次级模式",
                        "Aloic", null, "2025-11-13")
                        .addExample("/Setrule relax")
                        .addExample("/Setrule standard")
                        .addOption(new CommandParameter("Subruleset","指定的次级模式", CommandParameter.ParameterType.MUST)));
    }
}
