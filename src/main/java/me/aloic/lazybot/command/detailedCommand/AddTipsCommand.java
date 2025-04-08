package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.parameter.ContentParameter;
import me.aloic.lazybot.parameter.TipsParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"addtips"})
@Component
public class AddTipsCommand implements LazybotSlashCommand
{
    @Resource
    private ManageService manageService;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        ContentParameter params=new ContentParameter(OptionMappingTool.getOptionOrDefault(event.getOption("content"), "null"));
        params.setUserIdentity(event.getUser().getIdLong());
        params.validateParams();
        event.getHook().sendMessage(manageService.addTips(params)).queue();
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        ContentParameter params=ContentParameter.analyzeParameter(event.getCommandParameters());
        params.setUserIdentity(event.getMessageEvent().getSender().getUserId());
        params.validateParams();
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(manageService.addTips(params)).build(),false);
    }
}
