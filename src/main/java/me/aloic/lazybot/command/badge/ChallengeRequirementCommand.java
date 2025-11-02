package me.aloic.lazybot.command.badge;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.parameter.TipsParameter;
import me.aloic.lazybot.service.BadgeChallengeService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"cr","cd"})
@Component
public class ChallengeRequirementCommand implements LazybotSlashCommand
{
    @Resource
    private BadgeChallengeService badgeChallengeService;
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
        TipsParameter params = TipsParameter.analyzeParameter(event.getCommandParameters());
        if(params.getId()==null || params.getId()==0) {
            throw new IllegalArgumentException("id输入不合法");
        }
        CommandResultHandler.sendMessageWithImageToGroupOnebot(bot,event, badgeChallengeService.showRequirementsInChallenge(params.getId()));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        TipsParameter params = TipsParameter.analyzeParameter(event.getCommandParameters());
        if(params.getId()==null || params.getId()==0) {
            throw new IllegalArgumentException("id输入不合法");
        }
        testOutputTool.saveImageAndTextToLocal(badgeChallengeService.showRequirementsInChallenge(params.getId()));
    }


    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Challenge Requirement","Cr, Cd",
                        "查看指定Challenge需要完成的目标",
                        "Aloic", null, "2025-11-01")
                        .addExample("/Cr 1"));
    }

}
