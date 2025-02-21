package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.common.utils.MsgUtils;
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
import me.aloic.lazybot.osu.service.CustomizeService;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.parameter.CustomizationParameter;
import me.aloic.lazybot.parameter.UpdateParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"customize"})
@Component
public class CustomizeCommand implements LazybotSlashCommand
{
    @Resource
    private CustomizeService customizeService;
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
        String playerName = OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name());
        CustomizationParameter params=new CustomizationParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("type"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        event.getHook().sendMessage(customizeService.customize(params)).queue();
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO accessToken= tokenMapper.selectByQq_code(0L);
        AccessTokenPO tokenPO = tokenMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId());
        if (tokenPO == null)
            throw new RuntimeException("请先使用/link 你的用户名 绑定osu账号");
        tokenPO.setAccess_token(accessToken.getAccess_token());
        CustomizationParameter params=CustomizationParameter.analyzeParameter(event.getCommandParameters());
        CustomizationParameter.setupDefaultValue(params,tokenPO);
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(customizeService.customize(params)).build(),false);
    }
}
