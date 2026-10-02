package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.graphics.service.RasterizationService;
import me.aloic.lazybot.osu.dao.entity.vo.MapPerformanceAnalysis;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.BeatmapStatisticsParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.stereotype.Component;

import java.util.Optional;

@LazybotCommandMapping({"mp", "mpp"})
@Component
public class MapPerformanceAnalysisCommand implements LazybotSlashCommand {
    @Resource
    private PlayerService playerService;
    @Resource
    private RasterizationService rasterizationService;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
//        event.deferReply().queue();
//        BeatmapStatisticsParameter params = new BeatmapStatisticsParameter();
//        params.setBeatmapId(Optional.ofNullable(event.getOption("bid"))
//                .orElseThrow(() -> new IllegalArgumentException("bid为必选参数"))
//                .getAsInt());
//        params.setModCombination(optionString(event, "mods"));
//        params.setTargetAccuracy(optionDouble(event, "accuracy", 100.0));
//        params.setApproachRate(optionDouble(event, "ar", null));
//        params.setCircleSize(optionDouble(event, "cs", null));
//        params.setOverallDifficulty(optionDouble(event, "od", null));
//        params.setMode("osu");
//        params.validateParams();
//        MapPerformanceAnalysis analysis = playerService.getMapPpAnalysis(params);
//        CommandResultHandler.uploadImageToDiscord(event, rasterizationService.renderToMapPpAnalysis(analysis));
        // should not be impled now;
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception {
        MapPerformanceAnalysis analysis = playerService.getMapPpAnalysis(setupParameter(event));
        CommandResultHandler.uploadImageToOnebot(bot, event, rasterizationService.renderToMapPpAnalysis(analysis));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception {
        MapPerformanceAnalysis analysis = playerService.getMapPpAnalysis(setupParameter(event));
        testOutputTool.saveImageToLocal(rasterizationService.renderToMapPpAnalysis(analysis));
    }



    private static BeatmapStatisticsParameter setupParameter(LazybotSlashCommandEvent event) {
        BeatmapStatisticsParameter params = BeatmapStatisticsParameter.analyzeParameter(event.getCommandParameters());
        params.setMode(event.getOsuMode() == null ? "osu" : event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }


    @Override
    public String getHelp() {
        return HelpFormatter.format(
                new CommandHelp("Map PP Analysis", "Mp, Mpp",
                        "比较同一地图设置下进阶对比内容",
                        "Aloic", "Aloic", "2026-08-19")
                        .addExample("/mp 4889657")
                        .addExample("/mpp 4889657+HDHR 98.5")
                        .addExample("/mp 4889657+DT 99 AR9.5 CS4 OD8")
                        .addOption(new CommandParameter("BID", "查询的地图ID", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("Mod", "应用于全部计算的Mod组合", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("TargetAccuracy", "历史对比与Miss曲线的目标Accuracy", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("AR/CS/OD", "应用于全部计算的DA难度覆写", CommandParameter.ParameterType.OPTIONAL)));
    }
}
