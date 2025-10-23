package me.aloic.lazybot.command.badge;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;

import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.parameter.BadgeParameter;
import me.aloic.lazybot.service.BadgeService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"badge"})
@Component
public class BadgeCommand implements LazybotSlashCommand
{
    @Resource
    private BadgeService badgeService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
       //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        BadgeParameter badgeParameter=BadgeParameter.analyzeParameter(event.getCommandParameters());
        if (badgeParameter.getType()== BadgeParameter.BadgeActionType.LIST)
            CommandResultHandler.sendMessageToGroupOnebot(bot,event,
                    badgeService.showUserAllBadgeText(proxy.getAccessToken(event).getId())
            );
        else if (badgeParameter.getType()== BadgeParameter.BadgeActionType.VIEW)
            CommandResultHandler.sendMessageWithImageToGroupOnebot(bot,event,
                    badgeService.showUserOwnedSingleBadge(proxy.getAccessToken(event).getId(),badgeParameter.getIndex())
            );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        BadgeParameter badgeParameter=BadgeParameter.analyzeParameter(event.getCommandParameters());
        if (badgeParameter.getType()== BadgeParameter.BadgeActionType.LIST)
            testOutputTool.writeStringToFile(badgeService.showUserAllBadgeText(proxy.getAccessToken(event).getId()));
        else if (badgeParameter.getType()== BadgeParameter.BadgeActionType.VIEW)
            testOutputTool.saveImageAndTextToLocal(
                    badgeService.showUserOwnedSingleBadge(proxy.getAccessToken(event).getId(),badgeParameter.getIndex())
            );
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Badge","Badge",
                        "查询自己拥有的Badge，以及查询Badge详情",
                        "Aloic", null, "2025-10-22")
                        .addExample("/Badge list")
                        .addExample("/Badge view 1")
                        .addOption(new CommandParameter("Type","二级命令类型，List查看列表，View查看指定Badge详情", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("Index","仅限View，索引值", CommandParameter.ParameterType.OPTIONAL)));
    }

}
