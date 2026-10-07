package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.WhatIfParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@LazybotCommandMapping({"whatif"})
@Component
public class WhatIfCommand implements LazybotSlashCommand
{
    private final FunService funService;
    private final TestOutputTool testOutputTool;
    private final CommandDatabaseProxy proxy;

    public WhatIfCommand(FunService funService,
                         TestOutputTool testOutputTool,
                         CommandDatabaseProxy proxy)
    {
        this.funService = funService;
        this.testOutputTool = testOutputTool;
        this.proxy = proxy;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "WhatIf",
            List.of(),
            "{pp}[*][count]",
            "/whatif 300*10 400*5",
            "假设你多刷了这么多 pp 后的总 pp 变化",
            "可多项添加，数值不能大于 5000");
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        WhatIfParameter params=new WhatIfParameter();
        params.validateParams();
        event.getHook().sendMessage(funService.whatIfIGotSomePP(params)).queue();
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        WhatIfParameter params=WhatIfParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        WhatIfParameter.setupDefaultValue(params,proxy.getUserBinding(event));
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(funService.whatIfIGotSomePP(params)).build(),false);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        WhatIfParameter params= WhatIfParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        WhatIfParameter.setupDefaultValue(params,proxy.getUserBinding(event));
        testOutputTool.writeStringToFile(funService.whatIfIGotSomePP(params));
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("What if I got some pp","Whatif",
                        "假设你多刷了这么多pp后的总pp变化",
                        "Aloic", null, "2025-05-17")
                .code("CM-024")
                .headline("Daydreaming")
                .availability("ALL MODE")
                        .addExample("/Whatif 300*10 400*5")
                        .addOption(new CommandParameter("PP Series","Custom","给定的pp列表", CommandParameter.ParameterType.REQUIRED));
    }
}
