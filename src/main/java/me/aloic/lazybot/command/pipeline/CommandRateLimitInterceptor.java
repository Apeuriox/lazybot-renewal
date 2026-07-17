package me.aloic.lazybot.command.pipeline;

import me.aloic.lazybot.annotation.LazybotRateLimit;
import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.util.LazybotCommandRateLimitManager;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class CommandRateLimitInterceptor implements CommandInterceptor {
    private final LazybotCommandRateLimitManager rateLimitManager;

    public CommandRateLimitInterceptor(LazybotCommandRateLimitManager rateLimitManager) {
        this.rateLimitManager = rateLimitManager;
    }

    @Override
    public CommandResult intercept(
            CommandRequest request,
            PlatformIndependentCommand command,
            CommandInterceptorChain chain
    ) throws Exception {
        LazybotRateLimit rateLimit = AnnotationUtils.findAnnotation(command.getClass(), LazybotRateLimit.class);
        if (rateLimit == null) {
            return chain.proceed(request, command);
        }

        String key = switch (rateLimit.scope()) {
            case USER -> "user:" + request.context().userId() + ":cmd:" + request.commandName();
            case CHANNEL -> "channel:" + request.context().channelId() + ":cmd:" + request.commandName();
            case GLOBAL -> "global:cmd:" + request.commandName();
        };
        if (!rateLimitManager.tryConsume(key, rateLimit)) {
            throw new LazybotRuntimeException("达到速率限制，请稍后再试");
        }
        return chain.proceed(request, command);
    }
}
