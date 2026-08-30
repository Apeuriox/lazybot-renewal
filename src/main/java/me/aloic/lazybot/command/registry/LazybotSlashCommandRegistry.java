package me.aloic.lazybot.command.registry;

import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.annotation.SkipLazybotCommandPreprocessing;
import me.aloic.lazybot.command.LazybotSlashCommand;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
            if (mapping != null && mapping.value().length > 0) {
                String primaryName = normalize(mapping.value()[0]);
                RegisteredCommand registeredCommand =
                        new RegisteredCommand(command,
                                commandClass.isAnnotationPresent(SkipLazybotCommandPreprocessing.class),
                                primaryName
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

    public List<NamedCommand> listUniqueCommands()
    {
        Map<LazybotSlashCommand, NamedCommand> unique = new LinkedHashMap<>();
        commandMap.values().stream()
                .sorted(Comparator.comparing(RegisteredCommand::primaryName))
                .forEach(registered -> unique.putIfAbsent(
                        registered.command(),
                        new NamedCommand(registered.primaryName(), registered.command())));
        return new ArrayList<>(unique.values());
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
            boolean skipPreprocessing,
            String primaryName)
    {
    }

    public record NamedCommand(String primaryName, LazybotSlashCommand command)
    {
    }
}
