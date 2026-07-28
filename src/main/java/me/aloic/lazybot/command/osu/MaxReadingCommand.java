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
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"maxreading","mr"})
@Component
public class MaxReadingCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    private static final String MAXREADING_LABEL = "/MaxReading: Recalc Bps with max reading bonus. HD kept.";

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserTokenPO tokenPO = proxy.getDiscordBinding(event);
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
                        playerService.maxReading(GeneralParameter.setupParameter(event, proxy.getAccessToken(event))), 0, 3,
                        MAXREADING_LABEL)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderPlayerScoreListToCard(
                        playerService.maxReading(GeneralParameter.setupParameter(event, proxy.getAccessToken(event))), 0, 3,
                        MAXREADING_LABEL)
        );
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Max Reading", "MaxReading, mr",
                        "以最大化Reading奖励（AR读图加成）来计算用户的全部Bp。无变速mod→DA:AR11，DT/NC→DA:AR10，HT/DC→DA:AR0，HD保留",
                        "Aloic", "Aloic", "2026-06-18")
                        .addExample("/MaxReading")
                        .addExample("/Mr Aloic")
                        .addOption(new CommandParameter("PlayerName", "查询的玩家名称", CommandParameter.ParameterType.OPTIONAL)));
    }
}
