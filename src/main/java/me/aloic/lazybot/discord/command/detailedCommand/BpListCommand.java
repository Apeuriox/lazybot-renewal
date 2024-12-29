package me.aloic.lazybot.discord.command.detailedCommand;

import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.discord.command.LazybotSlashCommand;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.parameter.BpCommandParameter;
import me.aloic.lazybot.parameter.BplistCommandParameter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"bplist"})
@Component
public class BpListCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private TokenMapper tokenMapper;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
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
        BplistCommandParameter params=new BplistCommandParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("version"), 1),
                OptionMappingTool.getOptionOrDefault(event.getOption("from"), 0),
                OptionMappingTool.getOptionOrDefault(event.getOption("to"), 1));
        params.setPlayerId(OsuToolsUtil.getUserIdByUsername(playerName,tokenPO));
        params.setAccessToken(accessToken);
        ImageUploadUtil.uploadImageToDiscord(event,playerService.bplist(params));
    }
}
