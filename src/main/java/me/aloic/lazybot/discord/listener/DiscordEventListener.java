package me.aloic.lazybot.discord.listener;
import me.aloic.lazybot.component.EventListener;
import me.aloic.lazybot.component.SlashCommandProcessor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class DiscordEventListener extends ListenerAdapter implements EventListener
{
    private final SlashCommandProcessor slashCommandProcessor;

    public DiscordEventListener(SlashCommandProcessor slashCommandProcessor)
    {
        this.slashCommandProcessor = slashCommandProcessor;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        slashCommandProcessor.processDiscord(event);
    }
}
