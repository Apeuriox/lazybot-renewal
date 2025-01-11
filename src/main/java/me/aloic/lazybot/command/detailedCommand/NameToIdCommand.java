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
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.NameToIdParameter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Component
@LazybotCommandMapping({"nametoid","n2d"})
public class NameToIdCommand implements LazybotSlashCommand
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
        String playerNameList = OptionMappingTool.getOptionOrDefault(event.getOption("list"), tokenPO.getPlayer_name());
        List<String> playerNames = Arrays.stream(playerNameList.split(","))
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
        NameToIdParameter params=new NameToIdParameter(playerNames,"osu");
        params.setAccessToken(accessToken);
        params.validateParams();
        event.getHook().sendMessage(playerService.nameToId(params)).queue();
    }
}
