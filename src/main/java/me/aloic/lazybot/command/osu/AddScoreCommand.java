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
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.ScoreParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

//this command is to request recalculation of pp+
@LazybotCommandMapping({"addscores","addscore","add"})
@Component
public class AddScoreCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
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
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderAddScorePanel(
                playerService.addScoreForPerformancePlus(setupParameter(event, proxy.getUserBinding(event)))
                )
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderAddScorePanel(
                        playerService.addScoreForPerformancePlus(setupParameter(event, proxy.getUserBinding(event)))
                )
        );
    }
    protected static ScoreParameter setupParameter(LazybotSlashCommandEvent event, UserBindingPO tokenPO)
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
                new CommandHelp("Add Score","AddScore, AddScores, Add",
                        "以BID申请pp+重算，取最大结果",
                        "Aloic", "Aloic", "2025-07-22")
                        .addExample("/Addscore 4889657")
                        .addExample("/Add Aloic 4889657")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Bid","地图ID，仅支持STD模式", CommandParameter.ParameterType.MUST)));
    }
}
