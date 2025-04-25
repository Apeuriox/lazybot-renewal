package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.parameter.VerifyParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@LazybotCommandMapping({"verify"})
public class VerifyCommand implements LazybotSlashCommand
{
    @Resource
    private ManageService manageService;
    @Value("${lazybot.test.identity}")
    private Long identity;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {

    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        VerifyParameter params=VerifyParameter.analyzeParameter(event.getCommandParameters());
        params.setQqCode(event.getMessageEvent().getSender().getUserId());
        params.validateParams();
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(manageService.verify(params)).build(),false);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        VerifyParameter params=VerifyParameter.analyzeParameter(event.getCommandParameters());
        params.setQqCode(identity);
        params.validateParams();
        testOutputTool.writeStringToFile(manageService.verify(params));
    }
}
