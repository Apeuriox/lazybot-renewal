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
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusScore;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.RosuAlgorithmVersionUtil;
import me.aloic.lazybot.parameter.ScoreParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@LazybotCommandMapping({"score","s","pscore"})
@Component
public class ScoreCommand implements LazybotSlashCommand
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

        ScoreParameter params = new ScoreParameter(OptionMappingTool.getOptionOrDefault(event.getOption("mod"),""),
                Optional.ofNullable(event.getOption("bid")).orElseThrow(() -> new RuntimeException("bid为必选参数")).getAsInt(),
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("version"), 1),playerName);
        if (event.getOption("algorithm") != null) {
            params.setAlgorithmVersion(RosuAlgorithmVersionUtil.parse(event.getOption("algorithm").getAsString()));
        }
        params.validateParams();
        ScoreVO score = playerService.getUserHighestScoreOnMap(params);
        CommandResultHandler.uploadImageToDiscord(event, RendererDistributor.renderScoreVOToImage(score, params.getVersion()));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        ScoreParameter params=setupParameter(event,proxy.getAccessToken(event));
        if (event.getCommandType().equalsIgnoreCase("pscore") || params.getVersion() == 3) {
            PPPlusScore scorePlus =  playerService.getUserHighestScoreOnMapPlus(params);
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    RendererDistributor.renderPPPlusScoreToQuadraGrid(scorePlus)
            );
        }
        else
        {
            ScoreVO score=playerService.getUserHighestScoreOnMap(params);
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    RendererDistributor.renderScoreVOToImage(score, params.getVersion())
            );
        }
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        ScoreParameter params=setupParameter(event,proxy.getAccessToken(event));
        if (event.getCommandType().equalsIgnoreCase("pscore") || params.getVersion() == 3) {
            PPPlusScore scorePlus =  playerService.getUserHighestScoreOnMapPlus(params);
            testOutputTool.saveImageToLocal(RendererDistributor.renderPPPlusScoreToQuadraGrid(scorePlus));
        }
        else
        {
            ScoreVO score=playerService.getUserHighestScoreOnMap(params);
            testOutputTool.saveImageToLocal(RendererDistributor.renderScoreVOToImage(score, params.getVersion()));
        }

    }
    protected static ScoreParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO)
    {
        ScoreParameter params=ScoreParameter.analyzeParameter(event.getCommandParameters());
        ScoreParameter.setupDefaultValue(params,tokenPO);
        params.setVersion(event.getScorePanelVersion());
        if (tokenPO.getPreferred_panel_version()!=null)
            params.setVersion(tokenPO.getPreferred_panel_version());
        if (event.getScorePanelVersion()!=0)
            params.setVersion(event.getScorePanelVersion());
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        if (event.getMessageEvent()!=null)
            params.setChannelId(event.getMessageEvent().getGroupId());
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Score","Score, S, Pscore",
                        "按照指定用户查询指定地图下的指定Mod组合中分数最高的成绩, Pscore会以PP+数据返回",
                        "Aloic", "Slayemus, Aloic", "2024-04-06")
                        .addExample("/Score 4889657+HDHR")
                        .addExample("/s Aloic 4889657")
                        .addExample("/s Aloic 4889657+HD @202210")
                        .addExample("/Pscore Aloic 4889657+HDHR &")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("BID","查询的地图ID", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("Mod","Mod过滤项", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Algorithm","尾部传入 @202210/@202411/@202502/@202510/@20260706；省略时使用服务配置", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","&的出现次数，用于以其他样式的成绩面板返回结果", CommandParameter.ParameterType.OPTIONAL)));
    }
}
