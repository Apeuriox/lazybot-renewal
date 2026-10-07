package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.command.UserAllScore;
import me.aloic.lazybot.graphics.mapping.documentMapper.MapScoreSVGMapper;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.graphics.render.SVGRenderer;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.dao.entity.vo.MapScore;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.ScoreParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import me.aloic.lazybot.entity.CommandSummary;

@LazybotCommandMapping({"allscore","as","allscores","ass"})
@Component
public class AllScoreCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "AllScores",
            List.of("as"),
            "[userName] {bid}",
            "/as 668662",
            "查询对应玩家在对应地图下的全部成绩",
            "最大渲染 30 个");

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        ScoreParameter params = setupParameter(event, proxy.getUserBinding(event));
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderMapScore(playerService.getUserAllScoresOnMap(params),false)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        ScoreParameter params = setupParameter(event, proxy.getUserBinding(event));
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderMapScore(playerService.getUserAllScoresOnMap(params),false)
        );
    }
    protected static ScoreParameter setupParameter(LazybotSlashCommandEvent event, UserBindingPO tokenPO)
    {
        ScoreParameter params=ScoreParameter.analyzeParameter(event.getCommandParameters());
        params.applyAlgorithmVersion(event);
        ScoreParameter.setupDefaultValue(params, tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        if (event.getMessageEvent()!=null)
            params.setChannelId(event.getMessageEvent().getGroupId());
        else
            params.setChannelId(114514L);
        return params;
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("All Score","AllScore, AllScores, As, Ass",
                        "查询对应玩家在对应地图下的全部成绩，以及查询部分pp计算中间值",
                        "Aloic", "Aloic", "2025-06-03")
                .code("CM-025")
                .headline("Show All Scores on Map")
                .availability("ALL MODE")
                        .addExample("/Allscore 4889657")
                        .addExample("/As Aloic 4889657")
                        .addOption(new CommandParameter("PlayerName","String","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("BeatmapID","Integer","地图ID", CommandParameter.ParameterType.REQUIRED));
    }

}
