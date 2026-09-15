package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.graphics.service.RasterizationService;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerDailyDelta;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.service.PlayerStatisticsService;
import me.aloic.lazybot.osu.theme.preset.CardInfoColorPalette;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.ColorUtils;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@LazybotCommandMapping({"i"})
public class CardInfoCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private PlayerStatisticsService playerStatisticsService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;
    @Resource
    private RasterizationService rasterizationService;

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
        PlayerInfoVO info = playerService.getPlayerInfoVO(setupParameterGeneral(event, proxy.getUserBinding(event)));
        PlayerDailyDelta delta = playerStatisticsService.resolveDailyDelta(info);
        int[] rgb = ColorUtils.getDominantColorColorThief(new File(info.getAvatarUrl()));
        return rasterizationService.renderToCardInfo(info, delta, CardInfoColorPalette.fromRgb(rgb));
    }

    private GeneralParameter setupParameterGeneral(LazybotSlashCommandEvent event, UserBindingPO tokenPO)
    {
        GeneralParameter params=GeneralParameter.analyzeParameter(event.getCommandParameters());
        GeneralParameter.setupDefaultValue(params,tokenPO);
        params.setVersion(event.getScorePanelVersion());
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Card Info","i",
                        "查询个人资料, 生成小型卡片样式",
                        "Aloic", "Aloic", "2026-08-14 (Moelleux样式)")
                        .addExample("/i")
                        .addExample("/i Aloic")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL)));
    }
}
