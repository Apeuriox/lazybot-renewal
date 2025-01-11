package me.aloic.lazybot.command.detailedCommand;

import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.service.TrackService;
import me.aloic.lazybot.parameter.TopScoresParameter;
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
    private TokenMapper tokenMapper;

    @Override
    public void executeDiscord(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserTokenPO accessToken=tokenMapper.selectByDiscord(0L);
        TopScoresParameter params=new TopScoresParameter(OptionMappingTool.getOptionOrDefault(event.getOption("mode"),"osu"),
                OptionMappingTool.getOptionOrDefault(event.getOption("limit"), 10));
        params.setAccessToken(accessToken);
        params.validateParams();
        ImageUploadUtil.uploadImageToDiscord(event,trackService.bestPlaysInGamemode(params));
    }
}
