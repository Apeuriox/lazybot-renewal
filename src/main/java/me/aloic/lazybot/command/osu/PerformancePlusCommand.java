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
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@Component
@LazybotCommandMapping({"ppp","plus"})
public class PerformancePlusCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "Plus",
            List.of("ppp"),
            "[userName] [&]",
            "/plus Aloic &",
            "查询对应玩家的重算版 pp+，输入 & 以老样式输出结果",
            "颜色会跟随玩家主页颜色，默认为色域 208 的蓝色");

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        //not implemented
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        GeneralParameter params =  GeneralParameter.setupParameter(event, proxy.getUserBinding(event));
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderPerformancePlusCard(
                        playerService.getPerformancePlusPlayerInfo(params),params.getVersion())
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        GeneralParameter params =  GeneralParameter.setupParameter(event, proxy.getUserBinding(event));
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderPerformancePlusCard(
                        playerService.getPerformancePlusPlayerInfo(params),params.getVersion())
        );
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Performance Plus Card","Ppp, Plus",
                        "查询对应玩家的重算版pp+，输入&以最初版样式输出结果，主色调跟随玩家主页",
                        "Aloic", "Aloic", "2025-06-09")
                .code("CM-026")
                .headline("Performance Plus Profile")
                .availability("STD ONLY")
                        .addExample("/Plus")
                        .addExample("/Plus Aloic &")
                        .addExample("/Ppp &")
                        .addOption(new CommandParameter("PlayerName","String","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","Custom","存在 & 则以老形式输出", CommandParameter.ParameterType.OPTIONAL));
    }
}
