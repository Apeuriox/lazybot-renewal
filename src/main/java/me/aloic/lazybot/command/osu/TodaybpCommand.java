package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
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
import me.aloic.lazybot.parameter.TodaybpParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@LazybotCommandMapping({"todaybp","tbp","t"})
@Component
public class TodaybpCommand implements LazybotSlashCommand
{
    private final PlayerService playerService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;

    public TodaybpCommand(PlayerService playerService,
                          CommandDatabaseProxy proxy,
                          TestOutputTool testOutputTool)
    {
        this.playerService = playerService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "TodayBP",
            List.of("tbp"),
            "[userName] [#*num]",
            "/todaybp Aloic #10",
            "查询指定用户的 *num 天内的新增 Bp",
            "不填索引则默认为 #1");

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
        TodaybpParameter params=new TodaybpParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("days"), 1));
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event,RendererDistributor.renderPlayerScoreListToCard(
                playerService.getPlayerTodayNewBps(params),0,4,"Current command: /todayBp. Showing new Bps within " + params.getMaxDays() +" day(s)"));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        TodaybpParameter params = setupParameter(event,proxy.getUserBinding(event));
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderPlayerScoreListToCard(
                playerService.getPlayerTodayNewBps(params),0,4,"Current command: /todayBp. Showing new Bps within " + params.getMaxDays() +" day(s)"));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        TodaybpParameter params = setupParameter(event,proxy.getUserBinding(event));
        testOutputTool.saveImageToLocal(RendererDistributor.renderPlayerScoreListToCard(
                playerService.getPlayerTodayNewBps(params),0,4,"Current command: /todayBp. Showing new Bps within " + params.getMaxDays() +" day(s)"));
    }
    private TodaybpParameter setupParameter(LazybotSlashCommandEvent event,UserBindingPO tokenPO)
    {
        TodaybpParameter params=TodaybpParameter.analyzeParameter(event.getCommandParameters());
        TodaybpParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Today new bp","Todaybp, Tbp",
                        "查询指定用户的指定天内的新增Bp ",
                        "Aloic", "Aloic", "2025-12-11")
                .code("CM-050")
                .headline("New Bps in days")
                .availability("ALL MODE")
                        .addExample("/Tbp")
                        .addExample("/Tbp Aloic")
                        .addExample("/Tbp Aloic #10")
                        .addOption(new CommandParameter("PlayerName","String","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Index","Integer","查询的天数范围，默认为1", CommandParameter.ParameterType.OPTIONAL));
    }
}
