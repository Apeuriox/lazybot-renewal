package me.aloic.lazybot.command.detailedCommand;

import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.parameter.BplistParameter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"bpcard"})
@Component
public class BpCardCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private TokenMapper tokenMapper;

    @Override
    public void executeDiscord(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserTokenPO accessToken=tokenMapper.selectByDiscord(0L);
        UserTokenPO tokenPO = tokenMapper.selectByDiscord(event.getUser().getIdLong());
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
        tokenPO.setAccess_token(accessToken.getAccess_token());
        String playerName = OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name());
        BplistParameter params=new BplistParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("from"), 0),
                OptionMappingTool.getOptionOrDefault(event.getOption("to"), 1));
        params.setPlayerId(OsuToolsUtil.getUserIdByUsername(playerName,tokenPO));
        params.setAccessToken(accessToken);
        params.validateParams();
        ImageUploadUtil.uploadImageToDiscord(event,playerService.bplistCardView(params));
    }
}
