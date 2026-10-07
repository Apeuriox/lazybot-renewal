package me.aloic.lazybot.chain;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.chain.handler.CommandHandlerInterface;
import me.aloic.lazybot.chain.model.CommandHandlerChain;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandChainProcessor
{
    private final List<CommandHandlerInterface> handlers;

    public CommandChainProcessor(List<CommandHandlerInterface> handlers)
    {
        this.handlers = handlers;
    }

    public void process(LazybotSlashCommandEvent event, LazybotSlashCommand command) throws Exception {
        CommandHandlerChain chain = new CommandHandlerChain(handlers);
        chain.doHandle(event,command);
    }
    public void process(Bot bot, LazybotSlashCommandEvent event, LazybotSlashCommand command) throws Exception {
        CommandHandlerChain chain = new CommandHandlerChain(handlers);
        chain.doHandle(bot, event, command);
    }

}
