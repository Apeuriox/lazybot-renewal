package me.aloic.lazybot.discord.command.registry;

import me.aloic.lazybot.annotation.LazybotCommandMapping;

import me.aloic.lazybot.discord.command.LazybotSlashCommand;
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
                commandMap.put(mapping.value(), command);
            }
        }
    }

    public LazybotSlashCommand getCommand(String commandName) {
        return commandMap.get(commandName);
    }
}
