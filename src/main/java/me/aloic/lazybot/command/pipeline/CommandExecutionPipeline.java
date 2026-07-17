package me.aloic.lazybot.command.pipeline;

import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandExecutionPipeline {
    private final List<CommandInterceptor> interceptors;

    public CommandExecutionPipeline(List<CommandInterceptor> interceptors) {
        this.interceptors = List.copyOf(interceptors);
    }

    public CommandResult execute(CommandRequest request, PlatformIndependentCommand command) throws Exception {
        return new CommandInterceptorChain(interceptors).proceed(request, command);
    }
}
