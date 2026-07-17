package me.aloic.lazybot.command.osu;

import me.aloic.lazybot.command.core.CommandDefinition;
import me.aloic.lazybot.command.core.CommandOptionDefinition;
import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;
import me.aloic.lazybot.command.identity.CommandIdentityService;
import me.aloic.lazybot.command.parameter.BoundParameterFactory;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.service.AnalysisService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.util.HelpFormatter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecommendDifficultyCommand implements PlatformIndependentCommand {
    private final AnalysisService analysisService;
    private final CommandIdentityService commandIdentityService;
    private final BoundParameterFactory boundParameterFactory;

    public RecommendDifficultyCommand(
            AnalysisService analysisService,
            CommandIdentityService commandIdentityService,
            BoundParameterFactory boundParameterFactory
    ) {
        this.analysisService = analysisService;
        this.commandIdentityService = commandIdentityService;
        this.boundParameterFactory = boundParameterFactory;
    }

    @Override
    public CommandDefinition definition() {
        return CommandDefinition.discord(
                "rd",
                List.of("recommenddifficulty"),
                "查询指定模式下的推荐星数",
                List.of(
                        CommandOptionDefinition.string("user", "指定用户", false),
                        CommandOptionDefinition.string("mode", "指定查询的模式", false)
                )
        );
    }

    @Override
    public CommandResult execute(CommandRequest request) throws Exception {
        GeneralParameter params = boundParameterFactory.general(
                request,
                commandIdentityService.requireOsuIdentity(request.context())
        );
        return new CommandResult.Text(analysisService.recommendedDifficulty(params));
    }

    @Override
    public String getHelp() {
        return HelpFormatter.format(
                new CommandHelp("Recommend Difficulty", "rd, recommenddifficulty",
                        "查询指定用户的推荐星级，上为ppy算法，下为改进版",
                        "Aloic", null, "2025-01-07")
                        .addExample("/Rd")
                        .addExample("/Rd Aloic")
                        .addOption(new CommandParameter("PlayerName", "查询的玩家名称", CommandParameter.ParameterType.OPTIONAL)));
    }
}
