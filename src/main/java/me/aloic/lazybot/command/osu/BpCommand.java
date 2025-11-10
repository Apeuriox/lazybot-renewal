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
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.BpParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"bp","best","pbp","pb","b","bsm"})
@Component
public class BpCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserTokenPO accessToken= discordTokenMapper.selectByDiscord(0L);
        UserTokenPO tokenPO = discordTokenMapper.selectByDiscord(event.getUser().getIdLong());
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
        tokenPO.setAccess_token(accessToken.getAccess_token());
        String playerName = OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name());
        BpParameter params=new BpParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("version"), 0),
                OptionMappingTool.getOptionOrDefault(event.getOption("index"), 1));
        params.validateParams();
        params.setPlayerId(tokenPO.getPlayer_id());
        CommandResultHandler.uploadImageToDiscord(event,
                RendererDistributor.renderScoreVOToImage(
                        playerService.getUserBestPerformanceSingle(params),params.getVersion())
        );
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        String commandType = event.getCommandType().toLowerCase();
        BpParameter params = setupParameter(event,tokenPO);
        if (commandType.equals("pbp") || commandType.equals("pb")) {
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    RendererDistributor.renderPPPlusScoreToQuadraGrid(
                            playerService.getUserBestPerformanceSinglePlus(params))
            );
        }
        else
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    RendererDistributor.renderScoreVOToImage(
                            playerService.getUserBestPerformanceSingle(params), params.getVersion())
            );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        String commandType = event.getCommandType().toLowerCase();
        BpParameter params = setupParameter(event,tokenPO);
        if (commandType.equals("pbp") || commandType.equals("pb")) {
            testOutputTool.saveImageToLocal(RendererDistributor.renderPPPlusScoreToQuadraGrid(
                    playerService.getUserBestPerformanceSinglePlus(params)));
        }
        else if (commandType.equals("bsm"))
        {
            testOutputTool.saveImageToLocal(RendererDistributor.renderScoreVOToImage(
                    playerService.getUserBestPerformanceSingleStarMoon(params), params.getVersion()));
        }
        else
            testOutputTool.saveImageToLocal(RendererDistributor.renderScoreVOToImage(
                    playerService.getUserBestPerformanceSingle(params), params.getVersion()));
    }

    private BpParameter setupParameter(LazybotSlashCommandEvent event,AccessTokenPO tokenPO)
    {
        BpParameter params=BpParameter.analyzeParameter(event.getCommandParameters());
        BpParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setVersion(event.getScorePanelVersion());
        params.setPlayerId(tokenPO.getPlayer_id());
        params.validateParams();
        if (event.getMessageEvent()!=null)
            params.setChannelId(event.getMessageEvent().getGroupId());
        else
            params.setChannelId(114514L);
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Best Performance","Bp, Best, Pbp, Pb",
                        "查询指定用户的最佳成绩中的指定的第几个，Pbp即Pb会包含PP+数据",
                        "Aloic", "Slayemus, Aloic", "2024-04-06")
                        .addExample("/Bp #1")
                        .addExample("/Bp Aloic #10")
                        .addExample("/Bp Aloic #10 &")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Index","指定查询的索引，范围 1-200，默认为1", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","&的出现次数，用于以其他样式的成绩面板返回结果", CommandParameter.ParameterType.OPTIONAL)));
    }

}
