package me.aloic.lazybot.command.fun;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
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
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"tips"})
@SkipLazybotCommandPreprocessing
@Component
public class TipsCommand implements LazybotSlashCommand
{
    @Resource
    private FunService funService;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        TipsParameter params=new TipsParameter(OptionMappingTool.getOptionOrDefault(event.getOption("id"),0));
        params.validateParams();
        event.getHook().sendMessage(funService.tips(params)).queue();
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        TipsParameter params=TipsParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(funService.tips(params)).build(),false);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        TipsParameter params=TipsParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        testOutputTool.writeStringToFile(funService.tips(params));
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(new CommandHelp("Tips","tips",
                "返回一个随机的Aloic小提示，输入ID可明确指定，ID输入为空或者不合法会随机返回一个结果",
                "Aloic", null, "2025-01-20")
                .addExample("/tips 38")
                .addExample("/tips")
                .addOption(new CommandParameter("ID","指定查询Tips的ID", CommandParameter.ParameterType.OPTIONAL)));
    }
}
