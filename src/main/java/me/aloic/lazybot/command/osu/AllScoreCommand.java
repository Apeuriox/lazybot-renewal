package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.ScoreParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"allscore","as","allscores","ass"})
@Component
public class AllScoreCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        ImageUploadUtil.uploadImageToOnebot(bot,event,
                playerService.allScore(
                        setupParameter(event,
                                proxy.getAccessToken(event))
                )
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(playerService.allScore(
                        setupParameter(event,
                                proxy.getAccessToken(event))
                )
        );
    }
    protected static ScoreParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO)
    {
        ScoreParameter params=ScoreParameter.analyzeParameter(event.getCommandParameters());
        ScoreParameter.setupDefaultValue(params, tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("All Score","AllScore, AllScores, As, Ass",
                        "查询对应玩家在对应地图下的全部成绩，以及查询部分pp计算中间值",
                        "Aloic", "Aloic", "2025-06-03")
                        .addExample("/Allscore 4889657")
                        .addExample("/Allscore Aloic 4889657")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Bid","地图ID", CommandParameter.ParameterType.MUST)));
    }
}
