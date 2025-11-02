package me.aloic.lazybot.command.badge;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.service.BadgeChallengeService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"challenge","cl"})
@Component
public class ChallengeCommand implements LazybotSlashCommand
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
        CommandResultHandler.sendMessageWithImageToGroupOnebot(bot,event, badgeChallengeService.showAllActiveChallenges());
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageAndTextToLocal(badgeChallengeService.showAllActiveChallenges());
    }


    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Challenge","Challenge, Cl",
                        "查看当前所有可用的Challenge信息",
                        "Aloic", null, "2025-11-01")
                        .addExample("/Challenge"));
    }

}
