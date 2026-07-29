package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.monitor.CompareMonitor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.ScoreParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;


@LazybotCommandMapping({"c","compare"})
@Component
public class CompareCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
      // not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        ScoreParameter params = setupParameter(event, proxy.getUserBinding(event));
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderScoreVOToImage(
                playerService.getUserHighestScoreOnMap(params),params.getVersion())
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        ScoreParameter params = setupParameter(event, proxy.getUserBinding(event));
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderScoreVOToImage(
                        playerService.getUserHighestScoreOnMap(params),params.getVersion())
                );
    }
    private ScoreParameter setupParameter(LazybotSlashCommandEvent event,UserBindingPO tokenPO)
    {
        int index=1;
        if(!event.getCommandParameters().isEmpty()) {
            try{
                index = Integer.parseInt(event.getCommandParameters().getFirst());
            }
            catch (Exception e){
                throw new IllegalArgumentException("索引输入错误，请检查");
            }
        }
        if(index>5||index<0) {
            throw new IllegalArgumentException("索引范围为1至5");
        }
        ScoreParameter params=new ScoreParameter();
        if (event.getMessageEvent()!=null)
            params.setBeatmapId(CompareMonitor.getRecentBeatmap(event.getMessageEvent().getGroupId(), index));
        else
            params.setBeatmapId(CompareMonitor.getRecentBeatmap(114514L, index));
        ScoreParameter.setupDefaultValue(params,tokenPO);
        params.setVersion(event.getScorePanelVersion());
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        params.setChannelId(1919810L);
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Compare","c, compare",
                        "以最近的玩家查询成绩的同BID查询自己的成绩，输入数字以往前查询，最大为5",
                        "Aloic", "Slayemus, Aloic", "2025-08-31")
                        .addExample("/Compare")
                        .addExample("/Compare 2")
                        .addExample("/C 3")
                        .addOption(new CommandParameter("Index","查询的索引", CommandParameter.ParameterType.OPTIONAL))
        );
    }
}
