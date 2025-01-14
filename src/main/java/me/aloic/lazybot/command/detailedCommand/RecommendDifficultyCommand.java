package me.aloic.lazybot.command.detailedCommand;

import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.AnalysisService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.parameter.GeneralParameter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@LazybotCommandMapping({"rd","recommenddifficulty"})
public class RecommendDifficultyCommand implements LazybotSlashCommand
{
    @Resource
    private AnalysisService analysisService;
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
        GeneralParameter params=new GeneralParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        params.setInfoDTO(OsuToolsUtil.getUserInfoByUsername(playerName,tokenPO));
        params.setAccessToken(accessToken);
        params.validateParams();
        event.getHook().sendMessage(analysisService.recommendedDifficulty(params)).queue();
    }
}
