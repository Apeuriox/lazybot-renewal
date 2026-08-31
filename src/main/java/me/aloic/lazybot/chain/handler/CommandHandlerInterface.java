package me.aloic.lazybot.chain.handler;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.chain.model.CommandHandlerChain;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;

public interface CommandHandlerInterface
{
    /**
     * 处理命令
     * @param event 事件
     * @param chain 当前处理链
     */
    void handle(LazybotSlashCommandEvent event, LazybotSlashCommand command, CommandHandlerChain chain) throws Exception;
    void handle(Bot bot, LazybotSlashCommand command, LazybotSlashCommandEvent event, CommandHandlerChain chain) throws Exception;

}
