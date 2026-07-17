package me.aloic.lazybot.command.pipeline;

import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;

import java.util.Iterator;
import java.util.List;

public final class CommandInterceptorChain {
    private final Iterator<CommandInterceptor> iterator;

    public CommandInterceptorChain(List<CommandInterceptor> interceptors) {
        this.iterator = interceptors.iterator();
    }

    public CommandResult proceed(CommandRequest request, PlatformIndependentCommand command) throws Exception {
        if (iterator.hasNext()) {
            return iterator.next().intercept(request, command, this);
        }
        return command.execute(request);
    }
}
