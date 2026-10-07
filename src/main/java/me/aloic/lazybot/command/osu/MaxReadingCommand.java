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
import me.aloic.lazybot.entity.CommandSummary;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@LazybotCommandMapping({"maxread","mr","maxreading"})
@Component
public class MaxReadingCommand implements LazybotSlashCommand
{
    private final PlayerService playerService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;

    public MaxReadingCommand(PlayerService playerService,
                             CommandDatabaseProxy proxy,
                             TestOutputTool testOutputTool)
    {
        this.playerService = playerService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "MaxRead",
            List.of("mr"),
            "[userName]",
            "/mr Aloic",
            "以 AR 11 计算pp，主要给 2026 年 4 月更新前使用",
            "");

    private static final String MAXREADING_LABEL = "/MaxReading: Recalc Bps with max reading bonus. HD kept.";

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
        GeneralParameter params = new GeneralParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event,
                RendererDistributor.renderPlayerScoreListToCard(
                        playerService.maxReading(params), 0, 3,
                        MAXREADING_LABEL));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        CommandResultHandler.uploadImageToOnebot(bot, event,
                RendererDistributor.renderPlayerScoreListToCard(
                        playerService.maxReading(GeneralParameter.setupParameter(event, proxy.getUserBinding(event))), 0, 3,
                        MAXREADING_LABEL)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderPlayerScoreListToCard(
                        playerService.maxReading(GeneralParameter.setupParameter(event, proxy.getUserBinding(event))), 0, 3,
                        MAXREADING_LABEL)
        );
    }

    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Max Reading", "MaxReading, mr",
                        "以最大化Reading奖励（AR读图加成）来计算用户的全部Bp。无变速mod→DA:AR11，DT/NC→DA:AR10，HT/DC→DA:AR0，HD保留",
                        "Aloic", "Aloic", "2026-06-18")
                .code("CM-057")
                .headline("Maximize Your Reading PP")
                .availability("INCOMPLETE")
                        .addExample("/MaxReading")
                        .addExample("/Mr Aloic")
                        .addOption(new CommandParameter("PlayerName", "String", "查询的玩家名称", CommandParameter.ParameterType.OPTIONAL));
    }
}
