package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.command.core.CommandDefinition;
import me.aloic.lazybot.command.core.CommandOptionDefinition;
import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;
import me.aloic.lazybot.command.core.CommandContext;
import me.aloic.lazybot.command.core.CommandPlatform;
import me.aloic.lazybot.command.identity.BoundOsuIdentity;
import me.aloic.lazybot.command.identity.CommandIdentityService;
import me.aloic.lazybot.command.parameter.BoundParameterFactory;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.ProfileParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.List;

@Component
@LazybotCommandMapping({"profile","info"})
public class ProfileCommand implements LazybotSlashCommand, PlatformIndependentCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;
    @Resource
    private CommandIdentityService commandIdentityService;
    @Resource
    private BoundParameterFactory boundParameterFactory;

    @Override
    public CommandDefinition definition() {
        return CommandDefinition.discord(
                "profile",
                List.of("info"),
                "查询指定玩家的个人资料",
                List.of(
                        CommandOptionDefinition.string("user", "指定查询的用户", false),
                        CommandOptionDefinition.string("mode", "指定查询的模式", false),
                        CommandOptionDefinition.integer("version", "指定生成图像的风格", false)
                )
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        BoundOsuIdentity identity = commandIdentityService.requireOsuIdentity(new CommandContext(
                CommandPlatform.DISCORD,
                event.getUser().getId(),
                event.getChannel().getId(),
                UUID.randomUUID().toString()
        ));
        String playerName = OptionMappingTool.getOptionOrDefault(event.getOption("user"), identity.playerName());
        ProfileParameter params=new ProfileParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), identity.defaultMode())).getDescribe());
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event,
                RendererDistributor.renderProfileInfo(
                playerService.profile(params)));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderProfileInfo(
                playerService.profile(setupParameter(event, proxy.getAccessToken(event))))
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(RendererDistributor.renderProfileInfo(
                playerService.profile(setupParameter(event, proxy.getAccessToken(event))))
        );
    }

    @Override
    public CommandResult execute(CommandRequest request) throws Exception {
        BoundOsuIdentity identity = commandIdentityService.requireOsuIdentity(request.context());
        ProfileParameter params = boundParameterFactory.profile(request, identity);
        byte[] image = RendererDistributor.renderProfileInfo(playerService.profile(params));
        return new CommandResult.Image(image, "image/jpeg", "lazybot-profile.jpg", null);
    }

    private ProfileParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO)
    {
        ProfileParameter params=ProfileParameter.analyzeParameter(event.getCommandParameters());
        ProfileParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Player Info","Info, Profile",
                        "查询玩家的个人资料，背景可自定义",
                        "Aloic", "Slayemus, Aloic", "2025-02-12")
                        .addExample("/Profile")
                        .addExample("/Info Aloic")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL)));
    }
}
