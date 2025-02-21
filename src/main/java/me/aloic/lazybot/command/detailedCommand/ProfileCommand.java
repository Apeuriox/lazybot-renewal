package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.mapper.CustomizationMapper;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.ProfileParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.DataObjectExtractor;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@LazybotCommandMapping({"profile"})
public class ProfileCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private TokenMapper tokenMapper;
    @Resource
    private CustomizationMapper customizationMapper;
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
        ProfileParameter params=new ProfileParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        ImageUploadUtil.uploadImageToDiscord(event,playerService.profile(params));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO accessToken= tokenMapper.selectByQq_code(0L);
        AccessTokenPO tokenPO = tokenMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId());
        if (tokenPO == null)
            throw new RuntimeException("请使用/link 你的用户名 进行绑定");
        tokenPO.setAccess_token(accessToken.getAccess_token());
        ProfileParameter params=ProfileParameter.analyzeParameter(event.getCommandParameters());
        ProfileParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setAccessToken(accessToken.getAccess_token());
        params.validateParams();
        params.setInfoDTO(DataObjectExtractor.extractPlayerInfo(params.getAccessToken(),params.getPlayerName(),params.getMode()));
        ProfileCustomizationPO customization=customizationMapper.selectById(params.getInfoDTO().getId());
        params.setProfileCustomizationPO(customization);
        ImageUploadUtil.uploadImageToOnebot(bot,event,playerService.profile(params));
    }
}
