package me.aloic.lazybot.command.badge;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.parameter.*;
import me.aloic.lazybot.service.BadgeService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.AuthorityVerifier;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"bma","bmr", "bm"})
@Component
public class BadgeManageCommand implements LazybotSlashCommand
{
    @Resource
    private BadgeService badgeService;
    @Resource
    private TestOutputTool testOutputTool;

    @Value("${lazybot.test.identity}")
    private Long identity;
    @Value("${lazybot.test.enabled}")
    private Boolean testEnabled;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
       //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {

        if (event.getCommandType().equalsIgnoreCase("bma"))
        {
            BadgeActionParameter parameter = setupParameter(event);
            CommandResultHandler.sendMessageToGroupOnebot(bot,event, badgeService.addBadge(parameter));
        }

        if (event.getCommandType().equalsIgnoreCase("bmr"))
        {
            TipsParameter parameter = TipsParameter.analyzeParameter(event.getCommandParameters());
            CommandResultHandler.sendMessageToGroupOnebot(bot,event,
                    badgeService.removeBadge(parameter)
            );
        }
        if (event.getCommandType().equalsIgnoreCase("bm"))
        {
            BadgeImageParameter badgeParameter = BadgeImageParameter.analyzeParameter(event.getCommandParameters());
            CommandResultHandler.sendMessageToGroupOnebot(bot,event,
                    badgeService.addBadgeImageRemote(badgeParameter)
            );
        }

    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        if (event.getCommandType().equalsIgnoreCase("bma"))
        {
            BadgeActionParameter parameter = setupParameter(event);
            testOutputTool.writeStringToFile(badgeService.addBadge(parameter));
        }

        if (event.getCommandType().equalsIgnoreCase("bmr"))
        {
            TipsParameter parameter = TipsParameter.analyzeParameter(event.getCommandParameters());
            testOutputTool.writeStringToFile(badgeService.removeBadge(parameter));
        }
        if (event.getCommandType().equalsIgnoreCase("bm"))
        {
            BadgeImageParameter badgeParameter = BadgeImageParameter.analyzeParameter(event.getCommandParameters());
            testOutputTool.writeStringToFile(badgeService.addBadgeImageRemote(badgeParameter)
            );
        }
    }

    private BadgeActionParameter setupParameter(LazybotSlashCommandEvent event)
    {
        BadgeActionParameter params = BadgeActionParameter.analyzeParameter(event.getCommandParameters());
        if (!testEnabled) AuthorityVerifier.isAdmin(event.getMessageEvent().getSender().getUserId());
        else AuthorityVerifier.isAdmin(identity);
        return params;
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Badge Manage","Bma, Bmr, Bm",
                        "[管理员] 管理Badge",
                        "Aloic", null, "2025-10-22")
                        .addExample("/Bma {name=Test Badge} {desc=这是测试} {alt=Test} {type=0}")
                        .addExample("/Bmr 6")
                        .addExample("/Bm 2 https://this.is.link")
                        .addOption(new CommandParameter("Content","命令内容", CommandParameter.ParameterType.MUST)));
    }

}
