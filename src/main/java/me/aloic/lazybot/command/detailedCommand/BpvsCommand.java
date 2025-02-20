package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.parameter.BpvsParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"bpvs"})
@Component
public class BpvsCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private TokenMapper tokenMapper;

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
        BpvsParameter params=new BpvsParameter(OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name()),
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrException(event.getOption("target"), "请输入对比对象"));
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        ImageUploadUtil.uploadImageToDiscord(event,playerService.bpvs(params));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO accessToken= tokenMapper.selectByQq_code(0L);
        AccessTokenPO tokenPO = tokenMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId());
        OsuToolsUtil.linkedCheck(tokenPO);
        tokenPO.setAccess_token(accessToken.getAccess_token());
        BpvsParameter params=BpvsParameter.analyzeParameter(event.getCommandParameters());
        BpvsParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        ImageUploadUtil.uploadImageToOnebot(bot,event,playerService.bpvs(params));
    }
}
