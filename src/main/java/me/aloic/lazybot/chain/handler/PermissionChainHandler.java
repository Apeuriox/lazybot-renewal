package me.aloic.lazybot.chain.handler;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.Service.PermissionService;
import me.aloic.lazybot.chain.model.CommandHandlerChain;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(0)
public class PermissionChainHandler implements CommandHandlerInterface {

    @Resource
    private PermissionService permissionService;

    @Override
    public void handle(LazybotSlashCommandEvent event, LazybotSlashCommand command, CommandHandlerChain chain) throws Exception
    {
        if(!doCheck("TEST", 0L, command, event.getScorePanelVersion()))
        {
            throw new LazybotRuntimeException("[Lazybot] [TEST]权限检查失败，已停止执行");
        }
        chain.doHandle(event, command);

    }

    @Override
    public void handle(Bot bot, LazybotSlashCommand command, LazybotSlashCommandEvent event, CommandHandlerChain chain) throws Exception
    {
        if(!doCheck("CHANNEL", event.getMessageEvent().getGroupId(), command, event.getScorePanelVersion()))
        {
            throw new LazybotRuntimeException("[Lazybot] 此指令已在本群禁用");
        }
//        if(!doCheck("USER", event.getMessageEvent().getSender().getUserId(), command, event.getScorePanelVersion()))
//        {
//            throw new LazybotRuntimeException("[Lazybot] 此指令不允许该用户调用");
//        }
        if(!doCheck("GLOBAL", 0L, command, event.getScorePanelVersion()))
        {
            throw new LazybotRuntimeException("[Lazybot] 此指令已被开发者禁用");
        }
        chain.doHandle(bot, event, command);
    }
    private boolean doCheck(String type, Long id, LazybotSlashCommand command, Integer version)
    {
        try{
            if (version==null) version=0;
            return permissionService.checkPermission(type, id, command, version);
        }
        catch (Exception e)
        {
            throw new LazybotRuntimeException("[Lazybot] 权限检查失败，已跳过执行");
        }

    }
}
