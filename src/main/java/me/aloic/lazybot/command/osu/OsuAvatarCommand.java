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
import me.aloic.lazybot.command.identity.CommandIdentityService;
import me.aloic.lazybot.command.parameter.BoundParameterFactory;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@LazybotCommandMapping({"oa","avatar"})
public class OsuAvatarCommand implements LazybotSlashCommand, PlatformIndependentCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
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
                "oa",
                List.of("avatar"),
                "生成指定玩家的osu头像",
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
        UserTokenPO accessToken= discordTokenMapper.selectByDiscord(0L);
        UserTokenPO tokenPO = discordTokenMapper.selectByDiscord(event.getUser().getIdLong());
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
        tokenPO.setAccess_token(accessToken.getAccess_token());
        String playerName = OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name());
        GeneralParameter params=new GeneralParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event,
                RendererDistributor.renderOsuAvatar(
                        playerService.getPlayerInfoVO(params),0));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderOsuAvatar(
                        playerService.getPlayerInfoVO(setupParameter(event, proxy.getAccessToken(event))),event.getScorePanelVersion()));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
            testOutputTool.saveImageToLocal(
                     RendererDistributor.renderOsuAvatar(
                             playerService.getPlayerInfoVO(setupParameter(event, proxy.getAccessToken(event))),event.getScorePanelVersion()));
    }

    @Override
    public CommandResult execute(CommandRequest request) throws Exception {
        GeneralParameter params = boundParameterFactory.general(
                request,
                commandIdentityService.requireOsuIdentity(request.context())
        );
        byte[] image = RendererDistributor.renderOsuAvatar(
                playerService.getPlayerInfoVO(params),
                params.getVersion()
        );
        return new CommandResult.Image(image, "image/jpeg", "lazybot-avatar.jpg", null);
    }
    private GeneralParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO)
    {
        GeneralParameter params=GeneralParameter.analyzeParameter(event.getCommandParameters());
        GeneralParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Osu Avatar","oa, avatar",
                        "查看自己或他人的osu头像, 使用/update avatar即可更新，输入&将会包含pp和rank信息",
                        "Aloic", "Aloic", "2025-09-09")
                        .addExample("/oa")
                        .addExample("/oa Aloic")
                        .addExample("/oa &")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","存在&则会额外渲染pp和rank", CommandParameter.ParameterType.OPTIONAL)));
    }
}
