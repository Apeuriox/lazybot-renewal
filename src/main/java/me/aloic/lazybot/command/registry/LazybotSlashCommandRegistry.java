package me.aloic.lazybot.command.registry;

import me.aloic.lazybot.annotation.LazybotCommandMapping;

import me.aloic.lazybot.command.LazybotSlashCommand;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LazybotSlashCommandRegistry
{
    private final Map<String, LazybotSlashCommand> commandMap = new HashMap<>();

    public LazybotSlashCommandRegistry(ApplicationContext context)
    {
        Map<String, LazybotSlashCommand> beans = context.getBeansOfType(LazybotSlashCommand.class);
        for (LazybotSlashCommand command : beans.values()) {
            LazybotCommandMapping mapping = command.getClass().getAnnotation(LazybotCommandMapping.class);
            if (mapping != null) {
                for(String commandName : mapping.value()) {
                    commandMap.put(commandName, command);
                }
            }
        }
    }

    public LazybotSlashCommand getCommand(String commandName) {
        return commandMap.get(commandName);
    }
}
