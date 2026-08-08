package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Nonnull;
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
import me.aloic.lazybot.graphics.TemplateRenderer;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.OsuSubruleset;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.RosuAlgorithmVersionUtil;
import me.aloic.lazybot.parameter.BpParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"bp","best","pbp","b","bsm"})
@Component
public class BpCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;
    @Resource
    private TemplateRenderer templateRenderer;

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
        BpParameter params=new BpParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("version"), 0),
                OptionMappingTool.getOptionOrDefault(event.getOption("index"), 1));
        if (event.getOption("algorithm") != null) {
            params.setAlgorithmVersion(RosuAlgorithmVersionUtil.parse(event.getOption("algorithm").getAsString()));
        }
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
        UserBindingPO tokenPO;
        String commandType = event.getCommandType().toLowerCase();
        BpParameter params;
        if (commandType.equals("bsm"))
        {
            UserBindingPO starMoonToken = proxy.getStarMoonBinding(event);
            params = setupParameter(event,starMoonToken.getDefault_mode(),starMoonToken.getPlayer_id(), null);
            params.setSubRuleset(OsuSubruleset.getRuleset(starMoonToken.getDefault_subset()));
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    RendererDistributor.renderScoreVOToImage(
                            playerService.getUserBestPerformanceSingleStarMoon(params), params.getVersion())
            );
        }
        else
        {
            tokenPO=proxy.getUserBinding(event);
            params = setupParameter(event,tokenPO.getDefault_mode(),tokenPO.getPlayer_id(), tokenPO.getPreferred_panel_version());
            if (params.getVersion() == 3 || commandType.equals("pbp") || commandType.equals("pb"))
            {
                CommandResultHandler.uploadImageToOnebot(bot,event,
                        RendererDistributor.renderPPPlusScoreToQuadraGrid(
                                playerService.getUserBestPerformanceSinglePlus(params))
                );
            }
            else {
                CommandResultHandler.uploadImageToOnebot(bot,event,
                        RendererDistributor.renderScoreVOToImage(
                                playerService.getUserBestPerformanceSingle(params), params.getVersion()));
            }
        }

    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO tokenPO;
        String commandType = event.getCommandType().toLowerCase();
        BpParameter params;
        if (commandType.equals("bsm"))
        {
            UserBindingPO starMoonToken = proxy.getStarMoonBinding(event);
            params = setupParameter(event,starMoonToken.getDefault_mode(),starMoonToken.getPlayer_id(), null);
            params.setSubRuleset(OsuSubruleset.getRuleset(starMoonToken.getDefault_subset()));
            ScoreVO scoreVO = playerService.getUserBestPerformanceSingleStarMoon(params);
            byte[] imageBytes = templateRenderer.renderScore(scoreVO, params.getVersion());
            testOutputTool.saveImageToLocal(imageBytes);
        }
        else  {
            tokenPO=proxy.getUserBinding(event);
            params = setupParameter(event,tokenPO.getDefault_mode(),tokenPO.getPlayer_id(), tokenPO.getPreferred_panel_version());
            if (params.getVersion() == 3 || commandType.equals("pbp") || commandType.equals("pb"))
                testOutputTool.saveImageToLocal(RendererDistributor.renderPPPlusScoreToQuadraGrid(
                        playerService.getUserBestPerformanceSinglePlus(params)));
            else {
                ScoreVO scoreVO = playerService.getUserBestPerformanceSingle(params);
                byte[] imageBytes = templateRenderer.renderScore(scoreVO, params.getVersion());
                testOutputTool.saveImageToLocal(imageBytes);
            }
        }
    }

    private BpParameter setupParameter(LazybotSlashCommandEvent event, @Nonnull String mode, @Nonnull Integer playerId, Integer version)
    {
        BpParameter params=BpParameter.analyzeParameter(event.getCommandParameters());
        BpParameter.setupDefaultValue(params,mode);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setVersion(event.getScorePanelVersion());
        if (version!=null)
            params.setVersion(version);
        if (event.getScorePanelVersion()!=0)
            params.setVersion(event.getScorePanelVersion());
        params.setPlayerId(playerId);
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
                new CommandHelp("Best Performance","Bp, Best, Pbp",
                        "查询指定用户的最佳成绩中的指定的第几个，Pbp即Pb会包含PP+数据",
                        "Aloic", "Slayemus, Aloic", "2024-04-06")
                        .addExample("/Bp #1")
                        .addExample("/Bp Aloic #10")
                        .addExample("/Bp Aloic #10 @202502")
                        .addExample("/Bp Aloic #10 &")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Index","指定查询的索引，范围 1-200，默认为1", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Algorithm","尾部传入 @202210/@202411/@202502/@202510/@20260706；省略时使用服务配置", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","&的出现次数，用于以其他样式的成绩面板返回结果", CommandParameter.ParameterType.OPTIONAL)));
    }

}
