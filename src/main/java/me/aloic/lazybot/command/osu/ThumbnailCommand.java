package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.RecentParameter;
import me.aloic.lazybot.parameter.ThumbnailParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@LazybotCommandMapping({"tns","tnp"})
@Component
public class ThumbnailCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;


    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException
    {

    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws IOException
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        if (event.getCommandType().equalsIgnoreCase("tns"))
            ImageUploadUtil.uploadImageToOnebot(bot,event,
                    playerService.thumbnailClassicalScore(
                            setupParameter(event,tokenPO, 0))
            );
        else if (event.getCommandType().equalsIgnoreCase("tnp"))
        {
            ImageUploadUtil.uploadImageToOnebot(bot,event,
                    playerService.thumbnailClassicalRecent(
                            setupParameter(event,tokenPO, 1))
            );
        }

    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO = proxy.getAccessToken(event);
        if (event.getCommandType().equalsIgnoreCase("tns"))
        {
            testOutputTool.saveImageToLocal(playerService.thumbnailClassicalScore(
                    setupParameter(event, tokenPO, 0))
            );
         }
        else if (event.getCommandType().equalsIgnoreCase("tnp"))
        {
            testOutputTool.saveImageToLocal(playerService.thumbnailClassicalRecent(
                    setupParameter(event, tokenPO, 1))
            );
        }

    }
    private ThumbnailParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO,int type)
    {
        ThumbnailParameter params=ThumbnailParameter.analyzeParameter(event.getCommandParameters());
        ThumbnailParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setVersion(event.getScorePanelVersion());
        params.validateParams();
        if (type==0)
        {
            if(params.getBeatmapId()==null) {
                throw new IllegalArgumentException("bid输入值为空");
            }
            if(params.getBeatmapId()<=0) {
                throw new IllegalArgumentException("bid输入值不合法: " + params.getBeatmapId());
            }
        }
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Thumbnail","Tns, Thp",
                        "快捷生成视频封面,TNS以score形式选取，TBP以最近游玩形式选取",
                        "Aloic", "Alivemaster", "2025-09-26")
                        .addExample("/Pr #1")
                        .addExample("/Re Aloic #10")
                        .addExample("/Pr Aloic #10 &")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Index","指定查询的索引，范围 1-50，默认为1", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","&的出现次数，用于以其他样式的成绩面板返回结果", CommandParameter.ParameterType.OPTIONAL)));
    }

}
