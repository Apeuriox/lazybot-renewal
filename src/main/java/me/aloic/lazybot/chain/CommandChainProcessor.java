package me.aloic.lazybot.chain;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.chain.handler.CommandHandlerInterface;
import me.aloic.lazybot.chain.model.CommandHandlerChain;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandChainProcessor
{
    @Resource
    private List<CommandHandlerInterface> handlers;

    public void process(LazybotSlashCommandEvent event, LazybotSlashCommand command) throws Exception {
        CommandHandlerChain chain = new CommandHandlerChain(handlers);
        chain.doHandle(event,command);
    }
    public void process(Bot bot, LazybotSlashCommandEvent event, LazybotSlashCommand command) throws Exception {
        CommandHandlerChain chain = new CommandHandlerChain(handlers);
        chain.doHandle(bot, event, command);
    }

    public void processTencent(LazybotSlashCommandEvent event, LazybotSlashCommand command) throws Exception {
        CommandHandlerChain chain = new CommandHandlerChain(handlers);
        chain.doHandleTencent(event, command);
    }

}
