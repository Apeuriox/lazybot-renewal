package me.aloic.lazybot.command.badge;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.parameter.BadgeUserActionParameter;
import me.aloic.lazybot.parameter.BplistParameter;
import me.aloic.lazybot.parameter.ChallengeSubmitParameter;
import me.aloic.lazybot.service.BadgeChallengeService;
import me.aloic.lazybot.service.BadgeKeyService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.AuthorityVerifier;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"submit"})
@Component
public class SubmitCommand implements LazybotSlashCommand
{
    private final BadgeChallengeService badgeChallengeService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;


    public SubmitCommand(BadgeChallengeService badgeChallengeService,
                         CommandDatabaseProxy proxy,
                         TestOutputTool testOutputTool)
    {
        this.badgeChallengeService = badgeChallengeService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
       //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO token = proxy.getUserBinding(event);
        CommandResultHandler.sendMessageToGroupOnebot(bot,event, badgeChallengeService.checkUserSubmit(setupParameter(event,token)));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO token = proxy.getUserBinding(event);
        testOutputTool.writeStringToFile(badgeChallengeService.checkUserSubmit(setupParameter(event,token)));
    }

    private ChallengeSubmitParameter setupParameter(LazybotSlashCommandEvent event, UserBindingPO tokenPO)
    {
        ChallengeSubmitParameter params = ChallengeSubmitParameter.analyzeParameter(event.getCommandParameters());
        ChallengeSubmitParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }

    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Submit","Submit",
                        "提交成绩到指定的Challenge",
                        "Aloic", null, "2025-11-01")
                        .code("CM-046")
                        .addExample("/Submit 1 114514")
                        .addOption(new CommandParameter("ChallengeId","Integer","Challenge的ID，用于识别Challenge", CommandParameter.ParameterType.REQUIRED))
                        .addOption(new CommandParameter("BeatmapId","Integer","地图的ID", CommandParameter.ParameterType.REQUIRED));
    }

}
