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
import me.aloic.lazybot.parameter.BpvsParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@LazybotCommandMapping({"bpvs"})
@Component
public class BpvsCommand implements LazybotSlashCommand
{
    private final PlayerService playerService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;

    public BpvsCommand(PlayerService playerService,
                       CommandDatabaseProxy proxy,
                       TestOutputTool testOutputTool)
    {
        this.playerService = playerService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "Bpvs",
            List.of(),
            "{userName}",
            "/bpvs Aloic",
            "与指定用户的 BP 进行对比",
            "生成的图形为旧设计");

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserBindingPO tokenPO = proxy.getUserBinding(event);
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
        BpvsParameter params=new BpvsParameter(OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name()),
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrException(event.getOption("target"), "请输入对比对象"));
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event,
                RendererDistributor.renderComparePlayerBps(playerService.bpvs(params))
        );
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        BpvsParameter params = setupParameter(event, proxy.getUserBinding(event));
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderComparePlayerBps(playerService.bpvs(params))
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        BpvsParameter params = setupParameter(event, proxy.getUserBinding(event));
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderComparePlayerBps(playerService.bpvs(params))
        );
    }
    private BpvsParameter setupParameter(LazybotSlashCommandEvent event,UserBindingPO tokenPO)
    {
        BpvsParameter params=BpvsParameter.analyzeParameter(event.getCommandParameters());
        BpvsParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Bp Versus","Bpvs",
                        "与指定用户的BP进行对比，仅限Bp 1-100",
                        "Aloic", "Slayemus", "2024-04-25")
                .code("CM-008")
                .headline("Versus another user")
                .availability("ALL MODE")
                        .addExample("/Bpvs Aloic")
                        .addExample("/Bpvs Aloic#Apeuriox")
                        .addOption(new CommandParameter("Compare PlayerName","Custom","对比的玩家名称，可以以#分割输入两者", CommandParameter.ParameterType.REQUIRED));
    }
}
