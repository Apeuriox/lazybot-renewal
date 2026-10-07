package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.annotation.SkipLazybotCommandPreprocessing;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.CommandSummary;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.ThumbnailParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@LazybotCommandMapping({"tns","tnp","thumbnail"})
@SkipLazybotCommandPreprocessing
@Component
public class ThumbnailCommand implements LazybotSlashCommand
{
    private final PlayerService playerService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;

    public ThumbnailCommand(PlayerService playerService,
                            CommandDatabaseProxy proxy,
                            TestOutputTool testOutputTool)
    {
        this.playerService = playerService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "Thumbnail",
            List.of("tns","tnp"),
            "{key=value}",
            "/tns {id=2570594}",
            "快捷生成视频封面",
            "详情看 /tns *h");

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException
    {

    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws IOException
    {
        UserBindingPO tokenPO=proxy.getUserBinding(event);
        ThumbnailParameter params;
        if (event.getCommandType().equalsIgnoreCase("tns") || event.getCommandType().equalsIgnoreCase("thumbnail"))
        {
            params = setupParameter(event,tokenPO, 0);
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    RendererDistributor.renderThumbnailClassical(playerService.thumbnailClassicalScore(params)));
        }

        else if (event.getCommandType().equalsIgnoreCase("tnp"))
        {
            params = setupParameter(event,tokenPO, 1);
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    RendererDistributor.renderThumbnailClassical(playerService.thumbnailClassicalRecent(params)));
        }

    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO tokenPO = proxy.getUserBinding(event);
        ThumbnailParameter params;
        if (event.getCommandType().equalsIgnoreCase("tns"))
        {
            params = setupParameter(event,tokenPO, 0);
            testOutputTool.saveImageToLocal(RendererDistributor.renderThumbnailClassical(playerService.thumbnailClassicalScore(params)));
         }
        else if (event.getCommandType().equalsIgnoreCase("tnp"))
        {
            params = setupParameter(event,tokenPO, 1);
            testOutputTool.saveImageToLocal(RendererDistributor.renderThumbnailClassical(playerService.thumbnailClassicalScore(params)));
        }

    }
    private ThumbnailParameter setupParameter(LazybotSlashCommandEvent event, UserBindingPO tokenPO,int type)
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
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Thumbnail","Tns, Tnp",
                        "快捷生成视频封面,TNS以score形式选取，TNP以最近游玩形式选取，注意此指令的参数需要填写在{}中，具体请看示例",
                        "Aloic", "Alivemaster", "2025-09-26")
                .code("CM-036")
                .headline("Generate Video Thumbnail")
                .availability("ALL MODE")
                        .addExample("/Tns {id=2570594} {u=Aloic} {i=1} {p=123} {attr=ar od cs} {c=Comment Test}")
                        .addExample("/Tns {id=2570594}")
                        .addExample("/Tnp")
                        .addExample("/Tnp {u=Aloic} {i=2}")
                        .addOption(new CommandParameter("id","Integer","地图IO，仅限TNS", CommandParameter.ParameterType.REQUIRED))
                        .addOption(new CommandParameter("p","Integer","成绩的位次，默认为空", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("u","String","用户名，默认为自己", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("i","Integer","查询成绩的索引，由1开始，默认为1", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("c","String","评论文本，默认为空", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("attr","Custom","需要展示的地图参数，间隔符为空格，可选项为ar od cs hp length bpm，默认为cs和ar", CommandParameter.ParameterType.OPTIONAL));
    }

}
