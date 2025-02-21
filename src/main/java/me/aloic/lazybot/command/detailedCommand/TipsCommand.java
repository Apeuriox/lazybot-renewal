package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.TipsParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"tips"})
@Component
public class TipsCommand implements LazybotSlashCommand
{
    @Resource
    private FunService funService;

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
}
