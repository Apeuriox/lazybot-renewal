package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.service.TrackService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.parameter.TopScoresParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@LazybotCommandMapping({"topscores","ts"})
@Component
public class TopScoresCommand implements LazybotSlashCommand
{
    @Resource
    private TrackService trackService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private TokenMapper tokenMapper;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserTokenPO accessToken= discordTokenMapper.selectByDiscord(0L);
        TopScoresParameter params=new TopScoresParameter(OptionMappingTool.getOptionOrDefault(event.getOption("mode"),"osu"),
                OptionMappingTool.getOptionOrDefault(event.getOption("limit"), 10));
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        ImageUploadUtil.uploadImageToDiscord(event,trackService.bestPlaysInGamemode(params));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws IOException
    {
        AccessTokenPO accessToken= tokenMapper.selectByQq_code(0L);
        AccessTokenPO tokenPO = tokenMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId());
        OsuToolsUtil.linkedCheck(tokenPO);
        tokenPO.setAccess_token(accessToken.getAccess_token());
        TopScoresParameter params=TopScoresParameter.analyzeParameter(event.getCommandParameters());
        TopScoresParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        ImageUploadUtil.uploadImageToOnebot(bot,event,trackService.bestPlaysInGamemode(params));
    }
}
