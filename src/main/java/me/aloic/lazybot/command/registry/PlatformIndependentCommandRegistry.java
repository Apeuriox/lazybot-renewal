package me.aloic.lazybot.command.registry;

import me.aloic.lazybot.command.core.PlatformIndependentCommand;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** Registry for commands already migrated to the platform-independent contract. */
@Component
public class PlatformIndependentCommandRegistry {
    private final Map<String, PlatformIndependentCommand> commandMap = new HashMap<>();

    public PlatformIndependentCommandRegistry(ApplicationContext context) {
        context.getBeansOfType(PlatformIndependentCommand.class).values().forEach(command -> {
            for (String commandName : command.definition().allNames()) {
                PlatformIndependentCommand previous = commandMap.putIfAbsent(commandName.toLowerCase(), command);
                if (previous != null && previous != command) {
                    throw new IllegalStateException("Duplicate platform-independent command mapping: " + commandName);
                }
            }
        });
    }

    public PlatformIndependentCommand getCommand(String commandName) {
        return commandMap.get(commandName.toLowerCase());
    }

    public Collection<PlatformIndependentCommand> commands() {
        Set<PlatformIndependentCommand> commands = new LinkedHashSet<>(commandMap.values());
        return Set.copyOf(commands);
    }
}
