package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.service.TrackService;
import me.aloic.lazybot.parameter.TopScoresParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.IOException;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@LazybotCommandMapping({"topscores","ts"})
@Component
public class TopScoresCommand implements LazybotSlashCommand
{
    private final TrackService trackService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;

    public TopScoresCommand(TrackService trackService,
                            CommandDatabaseProxy proxy,
                            TestOutputTool testOutputTool)
    {
        this.trackService = trackService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "TopScores",
            List.of("ts"),
            "[max]",
            "/ts 20 :1",
            "查询指定模式的最高 Pp 成绩列表",
            "数据来源为 osu track");

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        TopScoresParameter params=new TopScoresParameter(OptionMappingTool.getOptionOrDefault(event.getOption("mode"),"osu"),
                OptionMappingTool.getOptionOrDefault(event.getOption("limit"), 10));
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event,
                RendererDistributor.renderPlayerScoreListToList(trackService.bestPlaysInGamemode(params)));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws IOException
    {
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderPlayerScoreListToList(
                        trackService.bestPlaysInGamemode(
                                setupParameter(event, proxy.getUserBinding(event))
                        )
                )
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(RendererDistributor.renderPlayerScoreListToList(
                        trackService.bestPlaysInGamemode(
                                setupParameter(event, proxy.getUserBinding(event))
                        )
                )
        );
    }
    private TopScoresParameter setupParameter(LazybotSlashCommandEvent event,UserBindingPO tokenPO)
    {
        TopScoresParameter params=TopScoresParameter.analyzeParameter(event.getCommandParameters());
        TopScoresParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Top scores in mode","Ts, Topscores",
                        "查询一个模式下最高pp的成绩列表，数据来源Osu Track，不一定准确",
                        "Aloic", "Aloic", "2025-01-11")
                .code("CM-018")
                .headline("Top PP Scores in Mode")
                .availability("ALL MODE")
                        .addExample("/Ts")
                        .addExample("/Ts 20")
                        .addOption(new CommandParameter("Index","Integer","最大索引范围，我会做一层过滤所以最终结果<=此内容", CommandParameter.ParameterType.OPTIONAL));
    }
}
