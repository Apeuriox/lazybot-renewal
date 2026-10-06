package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.CommandSummary;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.parameter.StatsParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@LazybotCommandMapping({"pstats"})
@Component
public class PlusPerformanceStatsCommand implements LazybotSlashCommand
{
    @Resource
    private ManageService manageService;
    @Resource
    private TestOutputTool testOutputTool;

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "Pstats",
            List.of("{type}"),
            "",
            "/pstats",
            "查询 PP+ 服务器统计数据",
            "统计类型: count/updated");

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        // not implemented for Discord
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        StatsParameter params = StatsParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        CommandResultHandler.sendMessageToGroupOnebot(bot, event, manageService.plusServerStats(params));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        StatsParameter params = StatsParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        testOutputTool.writeStringToFile(manageService.plusServerStats(params));
    }

    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("PP+ Stats", "pstats",
                        "查询PP+服务器统计数据",
                        "Aloic", null, "2026-05-21")
                .code("CM-056")
                .headline("PP+ Server Metadata")
                .availability("ALL MODE")
                        .addExample("/Pstats count")
                        .addExample("/Pstats updated")
                        .addOption(new CommandParameter("Type", "String", "统计类型: count/updated", CommandParameter.ParameterType.REQUIRED));
    }
}
