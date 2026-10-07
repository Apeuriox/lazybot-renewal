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
import me.aloic.lazybot.graphics.mapping.documentMapper.ScoreListSVGMapper;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.graphics.render.SVGRenderer;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.RosuAlgorithmVersionUtil;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import javax.swing.*;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@LazybotCommandMapping({"nochoke","nc","no1miss"})
@Component
public class NoChokeCommand implements LazybotSlashCommand
{
    private final PlayerService playerService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;

    public NoChokeCommand(PlayerService playerService,
                          CommandDatabaseProxy proxy,
                          TestOutputTool testOutputTool)
    {
        this.playerService = playerService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary NO_CHOKE = new CommandSummary(
            CommandSummary.Category.OSU,
            "NoChoke",
            List.of("nc"),
            "[userName]",
            "/noChoke Aloic",
            "将指定用户的 BP 100 按照 FC 重新计算，俗称幻想时刻",
            "渲染出的图形暂时只有 Fix 后的 BP");

    private static final CommandSummary NO_1_MISS = new CommandSummary(
            CommandSummary.Category.OSU,
            "No1Miss",
            List.of(),
            "[userName]",
            "/no1MIss Aloic",
            "将指定用户的 BP 中 <=1miss 的成绩按照 FC 重新计算",
            "此为 /noChoke 的限制版");

    private static final String NOCHOKE_LABEL = "All scores are recalculated with FC. Plz keep in mind that this may not reflect your skill correctly.";

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
        GeneralParameter params=new GeneralParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        if (event.getOption("algorithm") != null) {
            params.setAlgorithmVersion(RosuAlgorithmVersionUtil.parse(event.getOption("algorithm").getAsString()));
        }
        params.validateParams();
        if (event.getFullCommandName().equalsIgnoreCase("no1miss"))
            CommandResultHandler.uploadImageToDiscord(event,
                    RendererDistributor.renderPlayerScoreListToCard(
                    playerService.noChoke(params,1),0,2));
        else CommandResultHandler.uploadImageToDiscord(event,
                RendererDistributor.renderPlayerScoreListToCard(
                        playerService.noChoke(params,0),0,3,
                        NOCHOKE_LABEL));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        if (event.getCommandType().equalsIgnoreCase("no1miss"))
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    RendererDistributor.renderPlayerScoreListToCard(
                    playerService.noChoke(setupParameter(event),1),0,2)
            );
        else  CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderPlayerScoreListToCard(
                        playerService.noChoke(setupParameter(event),0),0,3,
                        NOCHOKE_LABEL)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        if (event.getCommandType().equalsIgnoreCase("no1miss"))
            testOutputTool.saveImageToLocal(
                    RendererDistributor.renderPlayerScoreListToCard(
                            playerService.noChoke(setupParameter(event),1),0,2)
            );
        else testOutputTool.saveImageToLocal(
                RendererDistributor.renderPlayerScoreListToCard(
                        playerService.noChoke(setupParameter(event),0),0,3,
                        NOCHOKE_LABEL)
        );
    }

    private GeneralParameter setupParameter(LazybotSlashCommandEvent event)
    {
        GeneralParameter params = GeneralParameter.setupParameter(
                event, proxy.getUserBinding(event));
        params.applyAlgorithmVersion(event);
        return params;
    }

    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("No Choke","NoChoke, nc, no1miss",
                        "以FC计算用户的全部Bp，使用no1miss仅计算<=1miss的成绩",
                        "Aloic", "Aloic", "2024-05-20")
                .code("CM-010")
                .headline("RECALC WITH FC")
                .availability("ALL MODE")
                        .addExample("/NoChoke")
                        .addExample("/NoChoke Aloic")
                        .addExample("/No1Miss Aloic")
                        .addExample("/NoChoke Aloic @202411")
                        .addOption(new CommandParameter("PlayerName","String","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Algorithm","Custom","以独立参数传入 @202210/@202411/@202502/@202510/@20260706；位置不限，省略时使用服务配置", CommandParameter.ParameterType.OPTIONAL));
    }

    @Override
    public java.util.List<CommandSummary> getCommandSummaries()
    {
        return java.util.List.of(NO_CHOKE, NO_1_MISS);
    }

}
