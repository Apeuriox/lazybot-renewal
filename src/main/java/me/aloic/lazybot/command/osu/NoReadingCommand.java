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
import me.aloic.lazybot.graphics.mapping.documentMapper.ScoreListSVGMapper;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.graphics.render.SVGRenderer;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"noreading","nr"})
@Component
public class NoReadingCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    private static final String NOREADING_LABEL = "/NoReading: Recalc Bps without read bonus. HD removed.";

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
                        playerService.noReading(params), 0, 3,
                        NOREADING_LABEL));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        CommandResultHandler.uploadImageToOnebot(bot, event,
                RendererDistributor.renderPlayerScoreListToCard(
                        playerService.noReading(GeneralParameter.setupParameter(event, proxy.getUserBinding(event))), 0, 3,
                        NOREADING_LABEL)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderPlayerScoreListToCard(
                        playerService.noReading(GeneralParameter.setupParameter(event, proxy.getUserBinding(event))), 0, 3,
                        NOREADING_LABEL)
        );
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("No Reading", "NoReading, nr",
                        "以去除Reading奖励（AR读图加成）来计算用户的全部Bp。DT/NC会设置DA:AR8.5，HT/DC会设置DA:AR10，HD会被移除",
                        "Aloic", "Aloic", "2026-06-18")
                        .addExample("/NoReading")
                        .addExample("/Nr Aloic")
                        .addOption(new CommandParameter("PlayerName", "查询的玩家名称", CommandParameter.ParameterType.OPTIONAL)));
    }
}
