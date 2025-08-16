package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.service.TrackService;
import me.aloic.lazybot.parameter.TopScoresParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@LazybotCommandMapping({"topscores","ts"})
@Component
public class TopScoresCommand implements LazybotSlashCommand
{
    @Resource
    private TrackService trackService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private CommandDatabaseProxy proxy;
    @Autowired
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserTokenPO accessToken= discordTokenMapper.selectByDiscord(0L);
        TopScoresParameter params=new TopScoresParameter(OptionMappingTool.getOptionOrDefault(event.getOption("mode"),"osu"),
                OptionMappingTool.getOptionOrDefault(event.getOption("limit"), 10));
        params.validateParams();
        ImageUploadUtil.uploadImageToDiscord(event,trackService.bestPlaysInGamemode(params));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws IOException
    {
        ImageUploadUtil.uploadImageToOnebot(bot,event,
                trackService.bestPlaysInGamemode(
                        setupParameter(event,
                                proxy.getAccessToken(event))
                )
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(trackService.bestPlaysInGamemode(
                        setupParameter(event,
                                proxy.getAccessToken(event))
                )
        );
    }
    private TopScoresParameter setupParameter(LazybotSlashCommandEvent event,AccessTokenPO tokenPO)
    {
        TopScoresParameter params=TopScoresParameter.analyzeParameter(event.getCommandParameters());
        TopScoresParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Top scores in mode","Ts, Topscores",
                        "查询一个模式下最高pp的成绩列表，数据来源Osu Track，不一定准确",
                        "Aloic", "Aloic", "2025-01-11")
                        .addExample("/Ts")
                        .addExample("/Ts 20")
                        .addOption(new CommandParameter("Index","最大索引范围，我会做一层过滤所以最终结果<=此内容", CommandParameter.ParameterType.OPTIONAL)));
    }
}
