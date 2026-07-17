package me.aloic.lazybot.command.fun;

import me.aloic.lazybot.command.core.CommandDefinition;
import me.aloic.lazybot.command.core.CommandOptionDefinition;
import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.TipsParameter;
import me.aloic.lazybot.util.HelpFormatter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TipsCommand implements PlatformIndependentCommand {
    private final FunService funService;

    public TipsCommand(FunService funService) {
        this.funService = funService;
    }

    @Override
    public CommandDefinition definition() {
        return CommandDefinition.discord(
                "tips",
                List.of(),
                "获取一个Aloic的小提示",
                List.of(CommandOptionDefinition.integer("id", "指定id", false))
        );
    }

    @Override
    public CommandResult execute(CommandRequest request) {
        TipsParameter params = request.arguments().integer("id")
                .map(TipsParameter::new)
                .orElseGet(() -> TipsParameter.analyzeParameter(request.positionalArguments()));
        params.validateParams();
        return new CommandResult.Text(funService.tips(params));
    }

    @Override
    public String getHelp() {
        return HelpFormatter.format(new CommandHelp("Tips", "tips",
                "返回一个随机的Aloic小提示，输入ID可明确指定，ID输入为空或者不合法会随机返回一个结果",
                "Aloic", null, "2025-01-20")
                .addExample("/tips 38")
                .addExample("/tips")
                .addOption(new CommandParameter("ID", "指定查询Tips的ID", CommandParameter.ParameterType.OPTIONAL)));
    }
}
