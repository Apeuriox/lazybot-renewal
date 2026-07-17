package me.aloic.lazybot.command.pipeline;

import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;

public interface CommandInterceptor {
    CommandResult intercept(
            CommandRequest request,
            PlatformIndependentCommand command,
            CommandInterceptorChain chain
    ) throws Exception;
}
