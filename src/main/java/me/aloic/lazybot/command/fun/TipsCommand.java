package me.aloic.lazybot.command.fun;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.annotation.SkipLazybotCommandPreprocessing;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.TipsParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@LazybotCommandMapping({"tips"})
@SkipLazybotCommandPreprocessing
@Component
public class TipsCommand implements LazybotSlashCommand
{
    private final FunService funService;
    private final TestOutputTool testOutputTool;

    public TipsCommand(FunService funService,
                       TestOutputTool testOutputTool)
    {
        this.funService = funService;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "Tips",
            List.of(),
            "[id|关键词]",
            "/tips 38",
            "返回一个随机的 Aloic 小提示，也可按内容搜索",
            "纯数字为 ID，其余按内容搜索");
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        TipsParameter params=new TipsParameter(OptionMappingTool.getOptionOrDefault(event.getOption("id"),0));
        String query = OptionMappingTool.getOptionOrDefault(event.getOption("query"), "");
        if (!query.isBlank()) {
            params.setQuery(query);
        }
        params.validateParams();
        event.getHook().sendMessage(funService.tips(params)).queue();
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        TipsParameter params=TipsParameter.analyzeTipsParameter(event.getCommandParameters());
        params.validateParams();
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(funService.tips(params)).build(),false);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        TipsParameter params=TipsParameter.analyzeTipsParameter(event.getCommandParameters());
        params.validateParams();
        testOutputTool.writeStringToFile(funService.tips(params));
    }

    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Tips","tips",
                "返回一个随机的Aloic小提示。纯数字按ID查询，其余文本按内容搜索，多个词都要命中。没有命中时直接提示，不会退回随机结果",
                "Aloic", null, "2025-01-20")
                .code("CM-019")
                .headline("Random Bullshit")
                .availability("ALL MODE")
                .addExample("/Tips 38")
                .addExample("/Tips")
                .addExample("/Tips pp 倍率")
                .addOption(new CommandParameter("ID","Integer","指定查询Tips的ID。与关键词同时传入时按ID查询", CommandParameter.ParameterType.OPTIONAL))
                .addOption(new CommandParameter("Query","String","按内容搜索Tips", CommandParameter.ParameterType.OPTIONAL));
    }
}
