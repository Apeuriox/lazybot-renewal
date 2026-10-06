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
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.SeriesParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@LazybotCommandMapping({"prs","rps","rs","res","ps"})
@Component
public class PlayRecentSeriesCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    private static final CommandSummary PRS = new CommandSummary(
            CommandSummary.Category.OSU,
            "Prs",
            List.of("rps", "ps"),
            "[userName] [&]",
            "/prs Aloic #1 &",
            "查询指定用户的最近 Pass 的 21 个成绩",
            "输入 & 采用 List 形式返回");

    private static final CommandSummary RES = new CommandSummary(
            CommandSummary.Category.OSU,
            "Res",
            List.of("rs"),
            "[userName] [&]",
            "/res Aloic",
            "查询指定用户的最近游玩的 21 个成绩",
            "输入 & 采用 List 形式返回");

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserBindingPO tokenPO = proxy.getUserBinding(event);
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
        String playerName = OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name());
        Integer style = OptionMappingTool.getOptionOrDefault(event.getOption("style"), 0);
        SeriesParameter params=new SeriesParameter(21,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                style,
                playerName);
        params.validateParams();
        if (event.getFullCommandName().equals("prs")||event.getFullCommandName().equals("rps")||event.getFullCommandName().equals("ps"))
            CommandResultHandler.uploadImageToDiscord(event,
                    RendererDistributor.renderPlayerScoreListToCard(
                            playerService.playRecentSeries(params,1, style),1,1));
        else
            CommandResultHandler.uploadImageToDiscord(event,
                    RendererDistributor.renderPlayerScoreListToList(
                            playerService.playRecentSeries(params,0, style),1));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO tokenPO=proxy.getUserBinding(event);
        String commandType=event.getCommandType().toLowerCase();
        SeriesParameter params=SeriesParameter.setupParameter(event,tokenPO.getPlayer_id(), tokenPO.getDefault_mode());
        int requestType =0;
        if (commandType.equals("rps")|| commandType.equals("prs")|| commandType.equals("ps")) {
            requestType=1;
        }
        if (event.getScorePanelVersion()==0)
        {
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    RendererDistributor.renderPlayerScoreListToCard(
                            playerService.playRecentSeries(params,requestType, 0),1,1));
        }
        else {
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    RendererDistributor.renderPlayerScoreListToList(
                            playerService.playRecentSeries(params,requestType, event.getScorePanelVersion()),1));
        }
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO tokenPO=proxy.getUserBinding(event);
        String commandType=event.getCommandType().toLowerCase();
        SeriesParameter params=SeriesParameter.setupParameter(event,tokenPO.getPlayer_id(), tokenPO.getDefault_mode());
        int requestType =0;
        if (commandType.equals("rps")|| commandType.equals("prs")|| commandType.equals("ps")) {
            requestType=1;
        }
        if (event.getScorePanelVersion()==0)
            testOutputTool.saveImageToLocal(
                    RendererDistributor.renderPlayerScoreListToCard(
                            playerService.playRecentSeries(params,requestType, 0),1,1));
        else
            testOutputTool.saveImageToLocal(
                    RendererDistributor.renderPlayerScoreListToList(
                            playerService.playRecentSeries(params,requestType, event.getScorePanelVersion()),1));

    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Play Recently Series","ps, rs, prs, rps, res",
                        "用于快速查询最近游玩中的1-21项，输入&以List形式返回",
                        "Aloic", "Aloic", "2024-07-23")
                .code("CM-013")
                .headline("Quick search of 21 Recent Plays")
                .availability("ALL MODE")
                        .addExample("/Ps")
                        .addExample("/Rs Aloic")
                        .addExample("/Ps &")
                        .addOption(new CommandParameter("PlayerName","String","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","Custom","存在&则以List形式输出", CommandParameter.ParameterType.OPTIONAL));
    }


    @Override
    public java.util.List<CommandSummary> getCommandSummaries()
    {
        return java.util.List.of(PRS, RES);
    }

}
