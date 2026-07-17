package me.aloic.lazybot.discord;

import me.aloic.lazybot.command.core.CommandResult;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

/** Delivers platform-neutral command results through an acknowledged Discord interaction. */
@Component
public class DiscordCommandResultDispatcher {
    public void dispatch(SlashCommandInteractionEvent event, CommandResult result) {
        if (result instanceof CommandResult.Text text) {
            event.getHook().sendMessage(text.content()).queue();
            return;
        }
        if (result instanceof CommandResult.Image image) {
            FileUpload upload = FileUpload.fromData(
                    new ByteArrayInputStream(image.content()),
                    image.fileName()
            );
            var action = event.getHook().sendFiles(upload);
            if (image.caption() != null && !image.caption().isBlank()) {
                action = action.setContent(image.caption());
            }
            action.queue();
            return;
        }
        if (result instanceof CommandResult.Composite composite) {
            composite.results().forEach(item -> dispatch(event, item));
            return;
        }
        if (result instanceof CommandResult.LegacySideEffect legacy) {
            event.getHook().sendMessage(legacy.message()).queue();
            return;
        }
        event.getHook().sendMessage("命令执行完成，无返回内容").setEphemeral(true).queue();
    }
}
