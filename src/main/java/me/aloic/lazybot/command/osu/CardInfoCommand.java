package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.CommandSummary;
import me.aloic.lazybot.graphics.service.RasterizationService;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerDailyDelta;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.service.PlayerStatisticsService;
import me.aloic.lazybot.osu.theme.preset.CardInfoColorPalette;
import me.aloic.lazybot.parameter.CardInfoParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.ColorUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@LazybotCommandMapping({"i","cardinfo"})
public class CardInfoCommand implements LazybotSlashCommand
{
    private final PlayerService playerService;
    private final PlayerStatisticsService playerStatisticsService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;
    private final RasterizationService rasterizationService;

    public CardInfoCommand(PlayerService playerService,
                           PlayerStatisticsService playerStatisticsService,
                           CommandDatabaseProxy proxy,
                           TestOutputTool testOutputTool,
                           RasterizationService rasterizationService)
    {
        this.playerService = playerService;
        this.playerStatisticsService = playerStatisticsService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
        this.rasterizationService = rasterizationService;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "CardInfo",
            List.of("i"),
            "[userName]",
            "/i Aloic",
            "查询个人资料，有历史资料对比",
            "");
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
       //not impl yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        CommandResultHandler.uploadImageToOnebot(bot, event, renderCardInfo(event));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(renderCardInfo(event));
    }

    private byte[] renderCardInfo(LazybotSlashCommandEvent event) throws Exception
    {
        CardInfoParameter params = CardInfoParameter.setupParameter(event, proxy.getUserBinding(event));
        PlayerInfoVO info = playerService.getPlayerInfoVO(params);
        PlayerDailyDelta delta = playerStatisticsService.resolveDailyDelta(info, params.getLookbackDays());
        int[] rgb = ColorUtils.getDominantColorColorThief(new File(info.getAvatarUrl()));
        return rasterizationService.renderToCardInfo(info, delta, CardInfoColorPalette.fromRgb(rgb));
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Card Info","i",
                        "查询个人资料, 生成小型卡片样式",
                        "Aloic", "Aloic", "2026-08-14")
                .code("CM-059")
                .headline("Small Profile Card")
                .availability("INCOMPLETE")
                        .addExample("/I")
                        .addExample("/I Aloic")
                        .addExample("/I #6")
                        .addExample("/I Aloic #12")
                        .addOption(new CommandParameter("PlayerName","String","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("#Days","Integer","对比N天前的快照；当天没有则取前后最近一条", CommandParameter.ParameterType.OPTIONAL));
    }
}
