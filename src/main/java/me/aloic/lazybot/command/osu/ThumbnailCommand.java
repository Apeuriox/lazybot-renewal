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
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.ThumbnailParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@LazybotCommandMapping({"tns","tnp","thumbnail"})
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
        if (event.getCommandType().equalsIgnoreCase("tns") || event.getCommandType().equalsIgnoreCase("thumbnail"))
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    playerService.thumbnailClassicalScore(
                            setupParameter(event,tokenPO, 0))
            );
        else if (event.getCommandType().equalsIgnoreCase("tnp"))
        {
            CommandResultHandler.uploadImageToOnebot(bot,event,
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
                new CommandHelp("Thumbnail","Tns, Tnp",
                        "快捷生成视频封面,TNS以score形式选取，TNP以最近游玩形式选取，注意此指令的参数需要填写在{}中，具体请看示例",
                        "Aloic", "Alivemaster", "2025-09-26")
                        .addExample("/Tns {id=2570594} {u=Aloic} {i=1} {p=123} {attr=ar od cs} {c=Comment Test}")
                        .addExample("/Tns {id=2570594}")
                        .addExample("/Tnp")
                        .addExample("/Tnp {u=Aloic} {i=2}")
                        .addOption(new CommandParameter("id","地图IO，仅限TNS", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("p","成绩的位次，默认为空", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("u","用户名，默认为自己", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("i","查询成绩的索引，由1开始，默认为1", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("c","评论文本，默认为空", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("attr","需要展示的地图参数，间隔符为空格，可选项为ar od cs hp length bpm，默认为cs和ar", CommandParameter.ParameterType.OPTIONAL)));
    }

}
