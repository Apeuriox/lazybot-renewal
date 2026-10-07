package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.CommandSummary;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapStatistics;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.BeatmapStatisticsParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.List;


@LazybotCommandMapping({"m","map"})
@Component
public class BeatmapStatisticsCommand implements LazybotSlashCommand
{
    private final PlayerService playerService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;

    public BeatmapStatisticsCommand(PlayerService playerService,
                                    CommandDatabaseProxy proxy,
                                    TestOutputTool testOutputTool)
    {
        this.playerService = playerService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "Map",
            List.of("m"),
            "{bid}+[mods] [acc] [CS/OD/AR]",
            "/m 3970329+DT",
            "查看指定地图的一些PP数据",
            "进阶查询请用/Mp");

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
     //
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        BeatmapStatisticsParameter params=setupParameter(event,proxy.getUserBinding(event));
        BeatmapStatistics bs=playerService.getBeatmapStatisticsWithImaginaryParams(params);
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderBeatmapStatisticsToImage(bs)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        BeatmapStatisticsParameter params=setupParameter(event,proxy.getUserBinding(event));
        BeatmapStatistics bs=playerService.getBeatmapStatisticsWithImaginaryParams(params);
        testOutputTool.saveImageToLocal(RendererDistributor.renderBeatmapStatisticsToImage(bs));
    }
    protected static BeatmapStatisticsParameter setupParameter(LazybotSlashCommandEvent event,UserBindingPO tokenPO)
    {
        BeatmapStatisticsParameter params=BeatmapStatisticsParameter.analyzeParameter(event.getCommandParameters());
        params.applyAlgorithmVersion(event);
        BeatmapStatisticsParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Beatmap Statistics","Map, M",
                        "查询指定地图在指定Mod组合下的参数，支持AR、CS、OD覆写",
                        "Aloic", "Slayemus, Aloic", "2026-04-13")
                .code("CM-054")
                .headline("Detailed PP Statistics")
                .availability("ALL MODE")
                        .addExample("/Map 4889657+HDHR 98.5 AR9.5 CS4 OD8")
                        .addExample("/M 4889657 AR 10 CS 4")
                        .addExample("/Map 4889657+HD 98.5 OD9 AR9.5 @202502")
                        .addExample("/Map 4889657")
                        .addOption(new CommandParameter("BeatmapID","Integer","查询的地图ID", CommandParameter.ParameterType.REQUIRED))
                        .addOption(new CommandParameter("Mod","String","Mod过滤项", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("TargetAccuracy","Float","申请额外重算的Acc", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("AR","Float","覆写AR值(0-11)，格式AR9.5或AR 10，注意会被特定Mod覆盖，如HR，EZ", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("CS","Float","覆写CS值(0-10)，格式CS4或CS 4", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("OD","Float","覆写OD值(0-11)，格式OD9或OD 9", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Algorithm","Custom","以独立参数传入 @202210/@202411/@202502/@202510/@20260706；位置不限，省略时使用最新算法", CommandParameter.ParameterType.OPTIONAL));
    }
}
