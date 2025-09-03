package me.aloic.lazybot.chain.handler;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.chain.model.CommandHandlerChain;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Order(1)
public class HelpChainHandler implements CommandHandlerInterface {
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void handle(LazybotSlashCommandEvent event, LazybotSlashCommand command, CommandHandlerChain chain) throws Exception {
        if (event.getCommandParameters() != null
                && !event.getCommandParameters().isEmpty()
                && "*help".equalsIgnoreCase(event.getCommandParameters().getFirst())) {
            testOutputTool.writeStringToFile(command.getHelp());
            return;
        }
        chain.doHandle(event, command);
    }

    @Override
    public void handle(Bot bot, LazybotSlashCommand command, LazybotSlashCommandEvent event, CommandHandlerChain chain) throws Exception
    {
        if (event.getCommandParameters()!=null
                && !event.getCommandParameters().isEmpty()
                && "*help".equalsIgnoreCase(event.getCommandParameters().getFirst())) {
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), command.getHelp(), false);
            return;
        }
        chain.doHandle(bot, event, command);
    }
}
