package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import lombok.NonNull;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.TokenStarMoon;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.osu.service.UserService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.UpdatePanelVersionParameter;
import me.aloic.lazybot.parameter.UpdateParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"setpanel","sp"})
@Component
public class UpdatePanelVersionCommand implements LazybotSlashCommand
{
    @Resource
    private UserService userService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
       //discord was almost given up
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO accessToken =  proxy.getAccessToken(event);
        CommandResultHandler.sendMessageToGroupOnebot(bot,event,
                userService.updatedUserPreferredPanelVersion(
                                setupParameter(event, accessToken)
                        )
                );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO accessToken =  proxy.getAccessToken(event);
        testOutputTool.writeStringToFile( userService.updatedUserPreferredPanelVersion(
                        setupParameter(event, accessToken)
                )
        );
    }
    private UpdatePanelVersionParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO token)
    {
        UpdatePanelVersionParameter params = (UpdatePanelVersionParameter) GeneralParameter.analyzeParameter(event.getCommandParameters());
        params.setQqCode(token.getQq_code());
        if (params.getPlayerName() == null)
            throw new LazybotRuntimeException("参数在哪");
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Set Score Panel Version","SetPanel, Sp",
                        "设置用户的默认成绩面板",
                        "Aloic", null, "2026-04-20")
                        .addExample("/SetPanel 1")
                        .addExample("/Sp 2")
                        .addExample("/Sp marathon")
                        .addOption(new CommandParameter("PanelVersion","面板的类型，支持数字和名称", CommandParameter.ParameterType.MUST)));
    }
}
