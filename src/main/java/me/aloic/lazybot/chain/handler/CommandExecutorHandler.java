package me.aloic.lazybot.chain.handler;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.chain.model.CommandHandlerChain;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class CommandExecutorHandler implements CommandHandlerInterface {
    @Override
    public void handle(LazybotSlashCommandEvent event, LazybotSlashCommand command, CommandHandlerChain chain) throws Exception {
        command.execute(event);
    }

    @Override
    public void handle(Bot bot, LazybotSlashCommand command, LazybotSlashCommandEvent event, CommandHandlerChain chain) throws Exception
    {
        command.execute(bot,event);
    }
}
