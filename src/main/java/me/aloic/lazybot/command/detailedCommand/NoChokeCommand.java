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
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"nochoke","nc","no1miss"})
@Component
public class NoChokeCommand implements LazybotSlashCommand
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
        GeneralParameter params=new GeneralParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        params.setInfoDTO(OsuToolsUtil.getUserInfoByUsername(playerName,tokenPO));
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        if (event.getFullCommandName().equalsIgnoreCase("no1miss"))
            ImageUploadUtil.uploadImageToDiscord(event,playerService.noChoke(params,1));
        else ImageUploadUtil.uploadImageToDiscord(event,playerService.noChoke(params,0));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        if (event.getCommandType().equalsIgnoreCase("no1miss"))
            ImageUploadUtil.uploadImageToOnebot(bot,event,
                    playerService.noChoke(
                            GeneralParameter.setupParameter(event, proxy.getAccessToken(event)), 1)
            );
        else  ImageUploadUtil.uploadImageToOnebot(bot,event,
                playerService.noChoke(
                        GeneralParameter.setupParameter(event, proxy.getAccessToken(event)), 0)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        if (event.getCommandType().equalsIgnoreCase("no1miss"))
            testOutputTool.saveImageToLocal(
                    playerService.noChoke(
                            GeneralParameter.setupParameter(event, proxy.getAccessToken(event)), 1)
            );
        else testOutputTool.saveImageToLocal(
                playerService.noChoke(
                        GeneralParameter.setupParameter(event, proxy.getAccessToken(event)), 0)
        );
    }
}
