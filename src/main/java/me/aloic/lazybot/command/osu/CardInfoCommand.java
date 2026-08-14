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
import me.aloic.lazybot.graphics.service.RasterizationService;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.theme.Color.HSL;
import me.aloic.lazybot.parameter.CardMoelleuxParameter;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.CommonTool;
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
        PlayerInfoVO info = playerService.getPlayerInfoVO(setupParameterGeneral(event, proxy.getUserBinding(event)));
        HSL mainHue = CommonTool.getDominantHSLColorThief(new File(info.getAvatarUrl()));
        boolean isTooDarkOrBright = mainHue.getSaturation()<4 || mainHue.getLightness()>94;
        CommandResultHandler.uploadImageToOnebot(bot,event,
                    rasterizationService.renderToCardInfo(info,mainHue.getHue(),isTooDarkOrBright ? 0 : 1));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        PlayerInfoVO info = playerService.getPlayerInfoVO(setupParameterGeneral(event, proxy.getUserBinding(event)));
        HSL mainHue = CommonTool.getDominantHSLColorThief(new File(info.getAvatarUrl()));
        boolean isTooDarkOrBright = mainHue.getSaturation()<4 || mainHue.getLightness()>94;
        testOutputTool.saveImageToLocal(
                rasterizationService.renderToCardInfo(info,mainHue.getHue(),isTooDarkOrBright ? 0 : 1));
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
                        "Aloic", "Aloic", "2024-03-22 (原版) / 2025-08-05 (Moelleux样式)")
                        .addExample("/Card")
                        .addExample("/Card Aloic")
                        .addExample("/Card Aloic hue=340 &&&")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Hue","覆盖默认取色算法的色相值，格式为hue=100，色相为0至360的整数，超出会取余", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","一个&则以原版样式输出，两个&将会禁用BP白色蒙层，三个&将强制以中等对比度输出，四个&以低对比度输出", CommandParameter.ParameterType.OPTIONAL)));
    }
}
