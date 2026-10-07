package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.CommandSummary;
import me.aloic.lazybot.osu.enums.OsuSubruleset;
import me.aloic.lazybot.osu.service.UserService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@LazybotCommandMapping({"setruleset","setrule",})
@Component
public class SetRulesetCommand implements LazybotSlashCommand
{
    private final UserService userService;

    public SetRulesetCommand(UserService userService)
    {
        this.userService = userService;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.PREFERENCE,
            "Setrule",
            List.of(),
            "{subRuleset}",
            "/setrule relax",
            "仅限Star Moon，更改默认次级模式",
            "relax 或 standard");
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        userService.updateDefaultSubset(event);
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        CommandResultHandler.sendMessageToGroupOnebot(bot, event,
                userService.updateDefaultSubset(
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
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Set Default Subruleset","Setruleset, Setrule",
                        "仅限Star Moon，更改默认次级模式",
                        "Aloic", null, "2025-11-13")
                .code("CM-049")
                .headline("Set Your Default Subruleset")
                .availability("ALL MODE")
                        .addExample("/Setrule relax")
                        .addExample("/Setrule standard")
                        .addOption(new CommandParameter("Subruleset","String","指定的次级模式", CommandParameter.ParameterType.REQUIRED));
    }
}
