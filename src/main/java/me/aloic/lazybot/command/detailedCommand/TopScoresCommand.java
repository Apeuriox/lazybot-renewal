package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.service.TrackService;
import me.aloic.lazybot.parameter.TopScoresParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
@LazybotCommandMapping({"topscores","ts"})
@Component
public class TopScoresCommand implements LazybotSlashCommand
{
    @Resource
    private TrackService trackService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;

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
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {

    }
}
