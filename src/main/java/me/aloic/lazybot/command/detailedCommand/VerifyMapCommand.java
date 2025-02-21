package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.parameter.BeatmapParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@LazybotCommandMapping({"verifymap","vm"})
public class VerifyMapCommand implements LazybotSlashCommand
{
    @Resource
    private ManageService manageService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private TokenMapper tokenMapper;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserTokenPO accessToken= discordTokenMapper.selectByDiscord(0L);
        BeatmapParameter params=new BeatmapParameter(Integer.parseInt(OptionMappingTool.getOptionOrDefault(event.getOption("bid"),"" )));
        params.setUserIdentity(event.getUser().getIdLong());
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        event.getHook().sendMessage(manageService.verifyBeatmap(params)).queue();
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO accessToken= tokenMapper.selectByQq_code(0L);
        BeatmapParameter params=BeatmapParameter.analyzeParameter(event.getCommandParameters());
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setUserIdentity(event.getMessageEvent().getSender().getUserId());
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(manageService.verifyBeatmap(params)).build(),false);
    }
}
