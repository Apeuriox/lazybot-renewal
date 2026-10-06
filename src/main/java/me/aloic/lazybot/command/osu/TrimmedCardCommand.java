package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.CommandSummary;
import me.aloic.lazybot.entity.command.MoelleuxCard;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.CardMoelleuxParameter;
import me.aloic.lazybot.service.CardService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@LazybotCommandMapping({"tc","trim"})
public class TrimmedCardCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "Trim",
            List.of("tc"),
            "[userName]",
            "/tc Aloic",
            "查询个人资料, 缩小版卡片样式",
            "");
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {

    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        CardMoelleuxParameter params = CardCommand.setupParameter(event, proxy.getUserBinding(event));
        CommandResultHandler.uploadImageToOnebot(bot, event,
                RendererDistributor.renderMMoelleuxCardTrimmed(playerService.cardMoelleuxTrimmed(params),2)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        CardMoelleuxParameter params = CardCommand.setupParameter(event, proxy.getUserBinding(event));
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderMMoelleuxCardTrimmed(playerService.cardMoelleuxTrimmed(params),2));
    }



    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Trimmed Card", "Tc",
                        "生成适合单独展示的裁剪版玩家卡片",
                        "Aloic", "Aloic", "2025-09-23")
                .code("CM-035")
                .headline("Trimmed Version of Small Card")
                .availability("STD ONLY")
                        .addExample("/tc")
                        .addExample("/tc Aloic")
                        .addExample("/tc Aloic hue=340")
                        .addOption(new CommandParameter(
                                "PlayerName", "String", "查询的玩家名称，留空使用已绑定账号",
                                CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter(
                                "Hue", "Custom", "覆盖卡片色相，格式为hue=100",
                                CommandParameter.ParameterType.OPTIONAL));
    }

}
