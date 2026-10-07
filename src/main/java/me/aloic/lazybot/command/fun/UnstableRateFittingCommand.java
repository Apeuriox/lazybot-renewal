package me.aloic.lazybot.command.fun;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.CommandSummary;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.DeviationFittingParameter;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@LazybotCommandMapping({"ur","accuracy"})
@Component
public class UnstableRateFittingCommand implements LazybotSlashCommand
{
    private final FunService funService;
    private final TestOutputTool testOutputTool;

    public UnstableRateFittingCommand(FunService funService,
                                      TestOutputTool testOutputTool)
    {
        this.funService = funService;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "UR",
            List.of(),
            "[od] {ur}",
            "/ur od9 100ur",
            "以理论计算当前UR在指定OD下的最佳acc表现",
            "");
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {

    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        DeviationFittingParameter params=DeviationFittingParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        CommandResultHandler.sendMessageToGroupOnebot(bot,event, funService.accuracyUsingNormalDistribution(params));

    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        DeviationFittingParameter params=DeviationFittingParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        testOutputTool.writeStringToFile(funService.accuracyUsingNormalDistribution(params));

    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Unstable Rate Fitting","ur, accuracy", "以理论计算当前UR在指定OD下的最佳acc表现","Aloic", null, "2026-03-07")
                .code("CM-052")
                .headline("Normal Distribution")
                .availability("ALL MODE")
                .addExample("/Ur od9 100ur")
                .addExample("/Accuracy 70")
                .addOption(new CommandParameter("OD","Float","OD值，最高13.33", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("UR","Float","UR值", CommandParameter.ParameterType.REQUIRED));
    }
}
