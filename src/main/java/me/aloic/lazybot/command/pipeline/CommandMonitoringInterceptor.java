package me.aloic.lazybot.command.pipeline;

import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;
import me.aloic.lazybot.monitor.CommandMonitor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class CommandMonitoringInterceptor implements CommandInterceptor {
    private final CommandMonitor commandMonitor;

    public CommandMonitoringInterceptor(CommandMonitor commandMonitor) {
        this.commandMonitor = commandMonitor;
    }

    @Override
    public CommandResult intercept(
            CommandRequest request,
            PlatformIndependentCommand command,
            CommandInterceptorChain chain
    ) throws Exception {
        commandMonitor.record(
                request.commandName(),
                request.context().userId(),
                request.context().channelId()
        );
        return chain.proceed(request, command);
    }
}
