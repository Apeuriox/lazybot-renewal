package me.aloic.lazybot.chain.handler;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotRateLimit;
import me.aloic.lazybot.chain.model.CommandHandlerChain;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.LazybotCommandRateLimitManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class RateLimitHandler implements CommandHandlerInterface {

    @Resource
    private LazybotCommandRateLimitManager rateLimitManager;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void handle(LazybotSlashCommandEvent event, LazybotSlashCommand command, CommandHandlerChain chain) throws Exception {
        LazybotRateLimit rateLimit = command.getClass().getAnnotation(LazybotRateLimit.class);
        if (rateLimit == null)
        {
            chain.doHandle(event, command);
            return;
        }

        String key = buildKey(rateLimit.scope(), event);

        if (!rateLimitManager.tryConsume(key, rateLimit)) {
            testOutputTool.writeStringToFile("[Lazybot] 达到速率限制，请等待20秒");
        }
        chain.doHandle(event, command);
    }

    private String buildKey(LazybotRateLimit.Scope scope, LazybotSlashCommandEvent event) {
        return switch (scope) {
            case USER -> "user:" + event.getMessageEvent().getSender().getUserId() + ":cmd:" + event.getCommandType();
            case CHANNEL -> "channel:" + event.getMessageEvent().getGroupId() + ":cmd:" + event.getCommandType();
            case GLOBAL -> "global:cmd:" + event.getCommandType();
        };
    }


    @Override
    public void handle(Bot bot, LazybotSlashCommand command, LazybotSlashCommandEvent event, CommandHandlerChain chain) throws Exception
    {
        LazybotRateLimit rateLimit = command.getClass().getAnnotation(LazybotRateLimit.class);
        if (rateLimit == null)
        {
            chain.doHandle(bot, event, command);
            return;
        }

        String key = buildKey(rateLimit.scope(), event);

        if (!rateLimitManager.tryConsume(key, rateLimit)) {
            CommandResultHandler.sendMessageToGroupOnebot(bot,event,"[Lazybot] 达到速率限制，请等待50秒");
            return;
        }
        chain.doHandle(bot, event, command);
    }
}
