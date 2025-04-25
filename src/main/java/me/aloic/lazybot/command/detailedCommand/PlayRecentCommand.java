package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.parameter.NameToIdParameter;
import me.aloic.lazybot.parameter.RecentParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@LazybotCommandMapping({"pr","rp","playrecent","rs","re","recent"})
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
    public void execute(SlashCommandInteractionEvent event) throws IOException
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
        params.setPlayerId(OsuToolsUtil.getUserIdByUsername(playerName,tokenPO));
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        if (event.getFullCommandName().equals("rp")||event.getFullCommandName().equals("pr")||event.getFullCommandName().equals("playrecent"))
            ImageUploadUtil.uploadImageToDiscord(event,playerService.recent(params,1));
        else
            ImageUploadUtil.uploadImageToDiscord(event,playerService.recent(params,0));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws IOException
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        if (event.getCommandType().equals("rp")||event.getCommandType().equals("pr")||event.getCommandType().equals("playrecent"))
            ImageUploadUtil.uploadImageToOnebot(bot,event,
                    playerService.recent(
                            setupParameter(event,tokenPO),
                            1)
            );
        else  ImageUploadUtil.uploadImageToOnebot(bot,event,
                playerService.recent(
                        setupParameter(event,tokenPO),
                        0)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        if (event.getCommandType().equals("rp")||event.getCommandType().equals("pr")||event.getCommandType().equals("playrecent"))
            testOutputTool.saveImageToLocal(playerService.recent(
                            setupParameter(event,tokenPO),
                            1)
            );
        else
            testOutputTool.saveImageToLocal(playerService.recent(
                setupParameter(event,tokenPO),
                0)
        );

    }
    private RecentParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO)
    {
        RecentParameter params=RecentParameter.analyzeParameter(event.getCommandParameters());
        RecentParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setVersion(event.getScorePanelVersion());
        params.setPlayerId(OsuToolsUtil.getUserIdByUsername(params.getPlayerName(),tokenPO));
        params.setAccessToken(tokenPO.getAccess_token());
        params.validateParams();
        return params;
    }


}
