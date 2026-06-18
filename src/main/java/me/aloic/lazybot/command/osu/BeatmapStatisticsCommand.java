package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapStatistics;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusScore;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.BeatmapStatisticsParameter;
import me.aloic.lazybot.parameter.ScoreParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@LazybotCommandMapping({"m","map"})
@Component
public class BeatmapStatisticsCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
     //
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        BeatmapStatisticsParameter params=setupParameter(event,proxy.getAccessToken(event));
        BeatmapStatistics bs=playerService.getBeatmapStatisticsWithImaginaryParams(params);
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderBeatmapStatisticsToImage(bs)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        BeatmapStatisticsParameter params=setupParameter(event,proxy.getAccessToken(event));
        BeatmapStatistics bs=playerService.getBeatmapStatisticsWithImaginaryParams(params);
        testOutputTool.saveImageToLocal(RendererDistributor.renderBeatmapStatisticsToImage(bs));
    }
    protected static BeatmapStatisticsParameter setupParameter(LazybotSlashCommandEvent event,AccessTokenPO tokenPO)
    {
        BeatmapStatisticsParameter params=BeatmapStatisticsParameter.analyzeParameter(event.getCommandParameters());
        BeatmapStatisticsParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Beatmap Statistics","Map, M",
                        "查询指定地图在指定Mod组合下的参数，支持AR覆写",
                        "Aloic", "Slayemus, Aloic", "2026-04-13")
                        .addExample("/Map 4889657+HDHR 98.5 AR9.5")
                        .addExample("/M 4889657 AR 10")
                        .addExample("/Map 4889657")
                        .addOption(new CommandParameter("BID","查询的地图ID", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("Mod","Mod过滤项", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("TargetAccuracy","申请额外重算的Acc", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("AR","覆写AR值(0-11)，格式AR9.5或AR 10，仅末尾支持", CommandParameter.ParameterType.OPTIONAL)));
    }
}
