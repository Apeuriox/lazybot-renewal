package me.aloic.lazybot.chain.model;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.chain.handler.CommandHandlerInterface;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;

import java.util.Iterator;
import java.util.List;

public class CommandHandlerChain {
    private final Iterator<CommandHandlerInterface> iterator;

    public CommandHandlerChain(List<CommandHandlerInterface> handlers) {
        this.iterator = handlers.iterator();
    }

    public void doHandle(LazybotSlashCommandEvent event, LazybotSlashCommand command) throws Exception {
        if (iterator.hasNext()) {
            CommandHandlerInterface next = iterator.next();
            next.handle(event, command ,this);
        }
    }
    public void doHandle(Bot bot, LazybotSlashCommandEvent event, LazybotSlashCommand command) throws Exception {
        if (iterator.hasNext()) {
            CommandHandlerInterface next = iterator.next();
            next.handle(bot, command, event ,this);
        }
    }

    public void doHandleTencent(LazybotSlashCommandEvent event, LazybotSlashCommand command) throws Exception {
        if (iterator.hasNext()) {
            CommandHandlerInterface next = iterator.next();
            next.handleTencent(event, command, this);
        }
    }

}
