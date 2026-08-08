package me.aloic.lazybot.command.registry;

import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.annotation.SkipLazybotCommandPreprocessing;
import me.aloic.lazybot.command.LazybotSlashCommand;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class LazybotSlashCommandRegistry
{
    private final Map<String, RegisteredCommand> commandMap = new HashMap<>();

    public LazybotSlashCommandRegistry(ApplicationContext context)
    {
        Map<String, LazybotSlashCommand> beans = context.getBeansOfType(LazybotSlashCommand.class);
        for (LazybotSlashCommand command : beans.values()) {
            Class<?> commandClass = AopProxyUtils.ultimateTargetClass(command);
            LazybotCommandMapping mapping =
                    commandClass.getAnnotation(LazybotCommandMapping.class);
            if (mapping != null) {
                RegisteredCommand registeredCommand =
                        new RegisteredCommand(command,
                                commandClass.isAnnotationPresent(SkipLazybotCommandPreprocessing.class)
                        );
                for(String commandName : mapping.value()) {
                    commandMap.put(normalize(commandName), registeredCommand);
                }
            }
        }
    }

    public LazybotSlashCommand getCommand(String commandName) {
        RegisteredCommand registeredCommand =
                commandMap.get(normalize(commandName));
        return registeredCommand == null
                ? null
                : registeredCommand.command();
    }

    public boolean shouldSkipPreprocessing(String commandName)
    {
        RegisteredCommand registeredCommand =
                commandMap.get(normalize(commandName));
        return registeredCommand != null
                && registeredCommand.skipPreprocessing();
    }

    private static String normalize(String commandName)
    {
        return commandName == null
                ? null
                : commandName.toLowerCase(Locale.ROOT);
    }

    private record RegisteredCommand(
            LazybotSlashCommand command,
            boolean skipPreprocessing)
    {
    }
}
