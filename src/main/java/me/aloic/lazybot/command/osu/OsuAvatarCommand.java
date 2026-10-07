package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.CommandSummary;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@LazybotCommandMapping({"oa","avatar"})
public class OsuAvatarCommand implements LazybotSlashCommand
{
    private final PlayerService playerService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;

    public OsuAvatarCommand(PlayerService playerService,
                            CommandDatabaseProxy proxy,
                            TestOutputTool testOutputTool)
    {
        this.playerService = playerService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "Avatar",
            List.of("oa"),
            "[userName]",
            "/oa Aloic",
            "查看自己或他人的 osu 头像",
            "使用 /update avatar 即可更新");

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserBindingPO tokenPO = proxy.getUserBinding(event);
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
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
                        playerService.getPlayerInfoVO(setupParameter(event, proxy.getUserBinding(event))),event.getScorePanelVersion()));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
            testOutputTool.saveImageToLocal(
                    RendererDistributor.renderOsuAvatar(
                            playerService.getPlayerInfoVO(setupParameter(event, proxy.getUserBinding(event))),event.getScorePanelVersion()));
    }
    private GeneralParameter setupParameter(LazybotSlashCommandEvent event, UserBindingPO tokenPO)
    {
        GeneralParameter params=GeneralParameter.analyzeParameter(event.getCommandParameters());
        GeneralParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Osu Avatar","oa, avatar",
                        "查看自己或他人的osu头像, 使用/update avatar即可更新，输入&将会包含pp和rank信息",
                        "Aloic", "Aloic", "2025-09-09")
                .code("CM-034")
                .headline("Link Your Osu! Account")
                .availability("ALL MODE")
                        .addExample("/Oa")
                        .addExample("/Oa Aloic")
                        .addExample("/Oa &")
                        .addOption(new CommandParameter("PlayerName","String","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","Custom","存在&则会额外渲染pp和rank", CommandParameter.ParameterType.OPTIONAL));
    }
}
