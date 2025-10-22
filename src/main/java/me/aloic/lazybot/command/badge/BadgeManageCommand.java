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
import me.aloic.lazybot.parameter.BadgeUserActionParameter;
import me.aloic.lazybot.service.BadgeService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"bm"})
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
        BadgeUserActionParameter parameter = setupParameter(event);
        if (parameter.getActionType() == BadgeUserActionParameter.BadgeManageType.ADDTOUSER)
            CommandResultHandler.sendMessageToGroupOnebot(bot,event,
                    badgeService.addBadgeToUser(parameter)
            );
        if (parameter.getActionType() == BadgeUserActionParameter.BadgeManageType.REMOVEFROMUSER)
            CommandResultHandler.sendMessageToGroupOnebot(bot,event,
                    badgeService.removeBadgeFromUser(parameter)
            );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        BadgeUserActionParameter parameter = setupParameter(event);
        if (parameter.getActionType() == BadgeUserActionParameter.BadgeManageType.ADDTOUSER)
            testOutputTool.writeStringToFile(badgeService.addBadgeToUser(parameter));
        else if (parameter.getActionType() == BadgeUserActionParameter.BadgeManageType.REMOVEFROMUSER)
            testOutputTool.writeStringToFile(badgeService.removeBadgeFromUser(parameter));
    }

    private BadgeUserActionParameter setupParameter(LazybotSlashCommandEvent event)
    {
        BadgeUserActionParameter params = BadgeUserActionParameter.analyzeParameter(event.getCommandParameters());
        if (!testEnabled) params.setUserIdentity(event.getMessageEvent().getSender().getUserId());
        else params.setUserIdentity(identity);
        return params;
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Badge Manage","BM",
                        "[管理员] 管理Badge",
                        "Aloic", null, "2025-10-22")
                        .addExample("/Bm at 11223344:2")
                        .addExample("/Bm at 11223344:2")
                        .addOption(new CommandParameter("Type","二级命令类型", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("Content","添加内容，格式为<playerId>:<badgeId>", CommandParameter.ParameterType.MUST)));
    }

}
