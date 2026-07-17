package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapStatistics;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.BeatmapStatisticsParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;


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
                        "查询指定地图在指定Mod组合下的参数，支持AR、CS、OD覆写",
                        "Aloic", "Slayemus, Aloic", "2026-04-13")
                        .addExample("/Map 4889657+HDHR 98.5 AR9.5 CS4 OD8")
                        .addExample("/M 4889657 AR 10 CS 4")
                        .addExample("/Map 4889657+HD 98.5 OD9 AR9.5 @202502")
                        .addExample("/Map 4889657")
                        .addOption(new CommandParameter("BID","查询的地图ID", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("Mod","Mod过滤项", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("TargetAccuracy","申请额外重算的Acc", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("AR","覆写AR值(0-11)，格式AR9.5或AR 10", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("CS","覆写CS值(0-10)，格式CS4或CS 4", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("OD","覆写OD值(0-11)，格式OD9或OD 9", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Algorithm","尾部传入 @202210/@202411/@202502/@202510/@20260706；省略时使用服务配置", CommandParameter.ParameterType.OPTIONAL)));
    }
}
