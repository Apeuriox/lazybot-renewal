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
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.BplistParameter;
import me.aloic.lazybot.util.HelpFormatter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BpListCommand implements PlatformIndependentCommand {
    private final PlayerService playerService;
    private final CommandIdentityService commandIdentityService;
    private final BoundParameterFactory boundParameterFactory;

    public BpListCommand(
            PlayerService playerService,
            CommandIdentityService commandIdentityService,
            BoundParameterFactory boundParameterFactory
    ) {
        this.playerService = playerService;
        this.commandIdentityService = commandIdentityService;
        this.boundParameterFactory = boundParameterFactory;
    }

    @Override
    public CommandDefinition definition() {
        return CommandDefinition.discord(
                "bplist",
                List.of(),
                "查询指定用户的指定from到to的bp",
                List.of(
                        CommandOptionDefinition.integer("from", "指定开始的位置", true),
                        CommandOptionDefinition.integer("to", "指定结束的位置", true),
                        CommandOptionDefinition.string("user", "指定查询的用户", false),
                        CommandOptionDefinition.string("mode", "指定查询的模式", false)
                )
        );
    }

    @Override
    public CommandResult execute(CommandRequest request) throws Exception {
        BplistParameter params = boundParameterFactory.bpList(
                request,
                commandIdentityService.requireOsuIdentity(request.context())
        );
        byte[] image = RendererDistributor.renderPlayerScoreListToList(
                playerService.bplistListView(params),
                params.getFrom()
        );
        return new CommandResult.Image(image, "image/jpeg", "lazybot-bplist.jpg", null);
    }

    @Override
    public String getHelp() {
        return HelpFormatter.format(
                new CommandHelp("Bp List List View", "Bplist",
                        "以指定范围查询用户的最佳成绩，以List列表形式返回",
                        "Aloic", "Aloic", "2024-04-27")
                        .addExample("/Bplist 1-21")
                        .addExample("/Bplist Aloic 1-21")
                        .addOption(new CommandParameter("PlayerName", "查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Range", "查询的范围，[num]-[num]", CommandParameter.ParameterType.MUST)));
    }
}
