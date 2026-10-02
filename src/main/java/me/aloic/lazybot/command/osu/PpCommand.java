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
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.RosuAlgorithmVersionUtil;
import me.aloic.lazybot.parameter.ScoreParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@LazybotCommandMapping({"pp"})
@Component
public class PpCommand implements LazybotSlashCommand
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
        event.deferReply().queue();
        UserBindingPO tokenPO = proxy.getUserBinding(event);
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
        String playerName = OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name());
        ScoreParameter params = new ScoreParameter(OptionMappingTool.getOptionOrDefault(event.getOption("mod"), ""),
                Optional.ofNullable(event.getOption("bid")).orElseThrow(() -> new RuntimeException("bid为必选参数")).getAsInt(),
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("version"), 1), playerName);
        if (event.getOption("algorithm") != null) {
            params.setAlgorithmVersion(RosuAlgorithmVersionUtil.parse(event.getOption("algorithm").getAsString()));
        }
        params.validateParams();
        ScoreVO score = playerService.getUserHighestPpOnMap(params);
        CommandResultHandler.uploadImageToDiscord(event, RendererDistributor.renderScoreVOToImage(score, params.getVersion()));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        ScoreParameter params = ScoreCommand.setupParameter(event, proxy.getUserBinding(event));
        ScoreVO score = playerService.getUserHighestPpOnMap(params);
        CommandResultHandler.uploadImageToOnebot(bot, event,
                RendererDistributor.renderScoreVOToImage(score, params.getVersion()));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        ScoreParameter params = ScoreCommand.setupParameter(event, proxy.getUserBinding(event));
        ScoreVO score = playerService.getUserHighestPpOnMap(params);
        testOutputTool.saveImageToLocal(RendererDistributor.renderScoreVOToImage(score, params.getVersion()));
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("PP", "pp",
                        "查询指定玩家在指定地图上PP最高的成绩，并使用成绩面板渲染",
                        "Aloic", "Aloic", "2026-09-14")
                        .addExample("/pp 4889657")
                        .addExample("/pp Aloic 4889657")
                        .addExample("/pp Aloic 4889657 @202510")
                        .addOption(new CommandParameter("PlayerName", "查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("BID", "查询的地图ID", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("Algorithm", "以独立参数传入 @202210/@202411/@202502/@202510/@20260706；位置不限，省略时使用服务配置", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version", "&的出现次数，用于以其他样式的成绩面板返回结果", CommandParameter.ParameterType.OPTIONAL)));
    }
}
