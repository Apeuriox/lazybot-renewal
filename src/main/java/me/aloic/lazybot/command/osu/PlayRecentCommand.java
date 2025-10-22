package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import desu.life.RosuFFI;
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
import me.aloic.lazybot.parameter.RecentParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@LazybotCommandMapping({"pr","rp","playrecent","re","recent","p","r","ppr","pre"})
@Component
public class PlayRecentCommand implements LazybotSlashCommand
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
    public void execute(SlashCommandInteractionEvent event) throws IOException, RosuFFI.FFIException
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
        RecentParameter params=new RecentParameter(OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("index"), 1),
                OptionMappingTool.getOptionOrDefault(event.getOption("version"), 1),playerName);
        params.validateParams();
        if (event.getFullCommandName().equals("rp")||event.getFullCommandName().equals("pr")||event.getFullCommandName().equals("playrecent")||event.getFullCommandName().equals("p"))
            CommandResultHandler.uploadImageToDiscord(event,playerService.recent(params,1));
        else if(event.getFullCommandName().equals("ppr"))
        {
            CommandResultHandler.uploadImageToDiscord(event,playerService.recentPlus(params,1));
        }
        else if (event.getFullCommandName().equals("pre"))
        {
            CommandResultHandler.uploadImageToDiscord(event,playerService.recentPlus(params,0));
        }
        else
            CommandResultHandler.uploadImageToDiscord(event,playerService.recent(params,0));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws IOException, RosuFFI.FFIException
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        String commandType = event.getCommandType().toLowerCase();
        switch (commandType) {
            case "rp" ,"pr" ,"playrecent" ->
                    CommandResultHandler.uploadImageToOnebot(bot, event,
                        playerService.recent(setupParameter(event, tokenPO), 1));
            case "ppr" ->
                CommandResultHandler.uploadImageToOnebot(bot, event,
                        playerService.recentPlus(setupParameter(event, tokenPO), 1));

            case "pre" ->
                CommandResultHandler.uploadImageToOnebot(bot, event,
                        playerService.recentPlus(setupParameter(event, tokenPO), 0));
            default ->
                CommandResultHandler.uploadImageToOnebot(bot, event,
                        playerService.recent(setupParameter(event, tokenPO), 0));

        }

    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        String commandType = event.getCommandType().toLowerCase();
        switch (commandType) {
            case "rp" ,"pr" ,"playrecent" ->
                    testOutputTool.saveImageToLocal(playerService.recent(
                            setupParameter(event,tokenPO),
                            1)
                    );
            case "ppr" ->
                    testOutputTool.saveImageToLocal(playerService.recentPlus(
                            setupParameter(event,tokenPO),
                            1)
                    );

            case "pre" ->
                    testOutputTool.saveImageToLocal(playerService.recentPlus(
                            setupParameter(event,tokenPO),
                            0)
                    );
            default ->
                    testOutputTool.saveImageToLocal(playerService.recent(
                            setupParameter(event,tokenPO),
                            0)
                    );

        }

    }
    private RecentParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO)
    {
        RecentParameter params=RecentParameter.analyzeParameter(event.getCommandParameters());
        RecentParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setVersion(event.getScorePanelVersion());
        params.validateParams();
        if (event.getMessageEvent()!=null)
            params.setChannelId(event.getMessageEvent().getGroupId());
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Play Recently","Pr, Rp, Playrecent, Re, Recent, P, R, Ppr, Pre",
                        "查询指定用户的最近游玩成绩中的指定的第几个,Ppr即Pre会包含PP+数据",
                        "Aloic", "Slayemus, Aloic", "2024-04-06")
                        .addExample("/Pr #1")
                        .addExample("/Re Aloic #10")
                        .addExample("/Pr Aloic #10 &")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Index","指定查询的索引，范围 1-50，默认为1", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","&的出现次数，用于以其他样式的成绩面板返回结果", CommandParameter.ParameterType.OPTIONAL)));
    }

}
