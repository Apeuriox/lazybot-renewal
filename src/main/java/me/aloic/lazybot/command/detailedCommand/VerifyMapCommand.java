package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.parameter.BeatmapParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Value;
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
    @Resource
    private TestOutputTool testOutputTool;
    @Value("${lazybot.test.identity}")
    private Long identity;
    @Value("${lazybot.test.enabled}")
    private Boolean testEnabled;


    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserTokenPO accessToken= discordTokenMapper.selectByDiscord(0L);
        BeatmapParameter params=new BeatmapParameter(Integer.parseInt(OptionMappingTool.getOptionOrDefault(event.getOption("bid"),"" )));
        params.setUserIdentity(event.getUser().getIdLong());
        params.validateParams();
        event.getHook().sendMessage(manageService.verifyBeatmap(params)).queue();
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(),
                MsgUtils.builder().text(
                        manageService.verifyBeatmap(
                                setupParameter(event,
                                        tokenMapper.selectByQq_code(0L))
                        )
                ).build(),false);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.writeStringToFile(manageService.verifyBeatmap(
                setupParameter(event,
                        tokenMapper.selectByQq_code(0L))
        ));
    }
    private BeatmapParameter setupParameter(LazybotSlashCommandEvent event,AccessTokenPO tokenPO)
    {
        BeatmapParameter params=BeatmapParameter.analyzeParameter(event.getCommandParameters());
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        if (!testEnabled) params.setUserIdentity(event.getMessageEvent().getSender().getUserId());
        else params.setUserIdentity(identity);
        params.validateParams();
        return params;
    }
}
