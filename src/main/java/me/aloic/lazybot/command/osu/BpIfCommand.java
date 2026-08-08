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
import me.aloic.lazybot.entity.command.PlayerScoreList;
import me.aloic.lazybot.graphics.mapping.documentMapper.ScoreListSVGMapper;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.graphics.render.SVGRenderer;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.AnalysisService;
import me.aloic.lazybot.osu.utils.RosuAlgorithmVersionUtil;
import me.aloic.lazybot.parameter.BpifParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@LazybotCommandMapping({"bpif"})
@Component
public class BpIfCommand implements LazybotSlashCommand
{
    @Resource
    private AnalysisService analysisService;
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
        BpifParameter params=new BpifParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("operator"), "+"),
                OptionMappingTool.getOptionOrDefault(event.getOption("mods"), ""),
                OptionMappingTool.getOptionOrDefault(event.getOption("rendersize"), 30));
        if (event.getOption("algorithm") != null) {
            params.setAlgorithmVersion(RosuAlgorithmVersionUtil.parse(event.getOption("algorithm").getAsString()));
        }
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event,
                RendererDistributor.renderPlayerScoreListToCard(
                        analysisService.bpIf(params),
                        0,
                        3,
                        "/BpIf: Recalculate your Bps with desired mods. +mod to insert, -mod to remove, !mod to replace.")
        );
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws IOException
    {
        UserBindingPO tokenPO=proxy.getUserBinding(event);
        BpifParameter params = setupParameter(event,tokenPO);
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderPlayerScoreListToCard(
                        analysisService.bpIf(params),
                        0,
                        3,
                        "/BpIf: Recalculate your Bps with desired mods. +mod to insert, -mod to remove, !mod to replace.")
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO tokenPO=proxy.getUserBinding(event);
        BpifParameter params = setupParameter(event,tokenPO);
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderPlayerScoreListToCard(
                        analysisService.bpIf(params),
                        0,
                        3,
                        "/BpIf: Recalculate your Bps with desired mods. +mod to insert, -mod to remove, !mod to replace.")
        );
    }

    private BpifParameter setupParameter(LazybotSlashCommandEvent event,UserBindingPO tokenPO)
    {
        BpifParameter params=BpifParameter.analyzeParameter(event.getCommandParameters());
        BpifParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setPlayerId(tokenPO.getPlayer_id());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Bp If Mods","Bpif",
                        "按照指定算法和Mod规则重算、排序全部BP并推演总PP；+添加，-删除，!替换Mod",
                        "Aloic", "Aloic", "2024-12-07")
                        .addExample("/Bpif +HD")
                        .addExample("/Bpif Aloic -HDHR")
                        .addExample("/Bpif !HDDT")
                        .addExample("/Bpif @202502")
                        .addExample("/Bpif +HD @202411")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Operator","运算符，与Mod不能有空格；仅指定算法时可以省略", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Mod","运算的Mod；仅指定算法版本时可以省略", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Algorithm","尾部传入 @202210/@202411/@202502/@202510/@20260706；省略时使用服务配置", CommandParameter.ParameterType.OPTIONAL)));
    }
}
