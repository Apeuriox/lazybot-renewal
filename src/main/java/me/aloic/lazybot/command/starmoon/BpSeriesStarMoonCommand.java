package me.aloic.lazybot.command.starmoon;

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
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.OsuSubruleset;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.BplistParameter;
import me.aloic.lazybot.parameter.SeriesParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"bssm"})
@Component
public class BpSeriesStarMoonCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {

    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
            UserBindingPO starMoonToken= proxy.getStarMoonBinding(event);
            SeriesParameter parameter = SeriesParameter.setupParameter(event,starMoonToken.getPlayer_id(), starMoonToken.getDefault_mode());
            parameter.applyAlgorithmVersion(event);
            BplistParameter params = new BplistParameter(parameter.getPlayerName(),
                    parameter.getPlayerId(),
                    parameter.getMode(),
                    1, parameter.getMaxIndex());
            params.setAlgorithmVersion(parameter.getAlgorithmVersion());
            params.setSubRuleset(OsuSubruleset.getRuleset(starMoonToken.getDefault_subset()));
            if (event.getScorePanelVersion()==0)
                CommandResultHandler.uploadImageToOnebot(bot,event,
                        RendererDistributor.renderPlayerScoreListToCard(playerService.bplistCardViewStarMoon(params),params.getFrom(),1));
            else
                CommandResultHandler.uploadImageToOnebot(bot,event,
                        RendererDistributor.renderPlayerScoreListToList(playerService.bplistListView(params), params.getFrom())
                );

    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO starMoonToken= proxy.getStarMoonBinding(event);
        SeriesParameter parameter = SeriesParameter.setupParameter(event,starMoonToken.getPlayer_id(), starMoonToken.getDefault_mode());
        parameter.applyAlgorithmVersion(event);
        BplistParameter params = new BplistParameter(parameter.getPlayerName(),
                    parameter.getPlayerId(),
                    parameter.getMode(),
                    1, parameter.getMaxIndex());
            params.setAlgorithmVersion(parameter.getAlgorithmVersion());
            params.setSubRuleset(OsuSubruleset.getRuleset(starMoonToken.getDefault_subset()));
            if (event.getScorePanelVersion()==0)
                testOutputTool.saveImageToLocal(RendererDistributor.renderPlayerScoreListToCard(playerService.bplistCardViewStarMoon(params),params.getFrom(),1));
            else
                testOutputTool.saveImageToLocal(RendererDistributor.renderPlayerScoreListToList(playerService.bplistListView(params), params.getFrom())
                );
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Bp Series Star Moon","Bssm",
                        "等效/Bpcard 1-[index]，用于快速查询，输入&以List形式返回，默认为21",
                        "Aloic", "Aloic", "2025-11-13")
                        .addExample("/Bssm")
                        .addExample("/Bssm Aloic")
                        .addExample("/Bssm Aloic 31 &")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Index","最大查询范围，默认21", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","存在&则以List形式输出", CommandParameter.ParameterType.OPTIONAL)));
    }


}
