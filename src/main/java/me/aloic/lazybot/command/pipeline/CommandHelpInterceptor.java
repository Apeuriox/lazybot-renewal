package me.aloic.lazybot.command.pipeline;

import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class CommandHelpInterceptor implements CommandInterceptor {
    @Override
    public CommandResult intercept(
            CommandRequest request,
            PlatformIndependentCommand command,
            CommandInterceptorChain chain
    ) throws Exception {
        if (!request.positionalArguments().isEmpty()) {
            String firstArgument = request.positionalArguments().getFirst();
            if ("*help".equalsIgnoreCase(firstArgument) || "*h".equalsIgnoreCase(firstArgument)) {
                return new CommandResult.Text(command.getHelp());
            }
        }
        return chain.proceed(request, command);
    }
}
