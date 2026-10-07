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
import me.aloic.lazybot.osu.utils.RosuAlgorithmVersionUtil;
import me.aloic.lazybot.parameter.BplistParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@LazybotCommandMapping({"bplist"})
@Component
public class BpListCommand implements LazybotSlashCommand
{
    private final PlayerService playerService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;

    public BpListCommand(PlayerService playerService,
                         CommandDatabaseProxy proxy,
                         TestOutputTool testOutputTool)
    {
        this.playerService = playerService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "Bplist",
            List.of(),
            "{*num-*num}",
            "/bplist 1-100",
            "查询用户最佳成绩中的范围",
            "暂不支持查询他人");
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserBindingPO tokenPO = proxy.getUserBinding(event);
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
        BplistParameter params=new BplistParameter(OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name()),
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("from"), 0),
                OptionMappingTool.getOptionOrDefault(event.getOption("to"), 1));
        if (event.getOption("algorithm") != null) {
            params.setAlgorithmVersion(RosuAlgorithmVersionUtil.parse(event.getOption("algorithm").getAsString()));
        }
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event,
                RendererDistributor.renderPlayerScoreListToList(playerService.bplistListView(params), params.getFrom()));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        BplistParameter params = setupParameter(event,proxy.getUserBinding(event));
        CommandResultHandler.uploadImageToOnebot(bot, event,
                RendererDistributor.renderPlayerScoreListToList(playerService.bplistListView(params), params.getFrom())
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        BplistParameter params = setupParameter(event,proxy.getUserBinding(event));
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderPlayerScoreListToList(playerService.bplistListView(params), params.getFrom())
        );
    }
    private BplistParameter setupParameter(LazybotSlashCommandEvent event, UserBindingPO tokenPO)
    {
        BplistParameter params=BplistParameter.analyzeParameter(event.getCommandParameters());
        params.applyAlgorithmVersion(event);
        BplistParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Bp List List View","Bplist",
                        "以指定范围查询用户的最佳成绩，以List列表形式返回",
                        "Aloic", "Aloic", "2024-04-27")
                .code("CM-009")
                .headline("Series of User Bests")
                .availability("ALL MODE")
                        .addExample("/Bplist 1-21")
                        .addExample("/Bplist Aloic 1-21")
                        .addExample("/Bplist Aloic 1-21 @202502")
                        .addOption(new CommandParameter("PlayerName","String","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Range","Custom","查询的范围，[num]-[num]", CommandParameter.ParameterType.REQUIRED))
                        .addOption(new CommandParameter("Algorithm","Custom","以独立参数传入 @202210/@202411/@202502/@202510/@20260706；位置不限，默认使用最新算法", CommandParameter.ParameterType.OPTIONAL));
    }
}
