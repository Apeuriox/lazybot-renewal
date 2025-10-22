package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
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

@LazybotCommandMapping({"prs","rps","rs","res","ps"})
@Component
public class PlayRecentSeriesCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;


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
        Integer style = OptionMappingTool.getOptionOrDefault(event.getOption("style"), 0);
        GeneralParameter params=new GeneralParameter(playerName,OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"),
                String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        params.validateParams();
        if (event.getFullCommandName().equals("prs")||event.getFullCommandName().equals("rps")||event.getFullCommandName().equals("ps"))
            CommandResultHandler.uploadImageToDiscord(event,playerService.playRecentSeries(params,1, style));
        else
            CommandResultHandler.uploadImageToDiscord(event,playerService.playRecentSeries(params,0, style));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        if (event.getCommandType().equalsIgnoreCase("rps")||
                event.getCommandType().equalsIgnoreCase("prs")||
                event.getCommandType().equalsIgnoreCase("ps"))
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    playerService.playRecentSeries(
                            GeneralParameter.setupParameter(event,tokenPO),
                            1, event.getScorePanelVersion())
            );
        else  CommandResultHandler.uploadImageToOnebot(bot,event,
                playerService.playRecentSeries(
                        GeneralParameter.setupParameter(event,tokenPO),
                        0, event.getScorePanelVersion())
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        if (event.getCommandType().equals("rps")||event.getCommandType().equals("prs")||event.getCommandType().equals("ps"))
            testOutputTool.saveImageToLocal(playerService.playRecentSeries(
                    GeneralParameter.setupParameter(event,tokenPO),
                    1, event.getScorePanelVersion())
            );
        else
            testOutputTool.saveImageToLocal(playerService.playRecentSeries(
                    GeneralParameter.setupParameter(event,tokenPO),
                    0, event.getScorePanelVersion())
        );

    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Play Recently Series","ps, rs, prs, rps, res",
                        "用于快速查询最近游玩中的1-21项，输入&以List形式返回",
                        "Aloic", "Aloic", "2024-07-23")
                        .addExample("/Ps")
                        .addExample("/Rs Aloic")
                        .addExample("/Ps &")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","存在&则以List形式输出", CommandParameter.ParameterType.OPTIONAL)));
    }


}
