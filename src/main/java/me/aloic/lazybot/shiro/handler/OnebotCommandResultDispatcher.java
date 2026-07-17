package me.aloic.lazybot.shiro.handler;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import org.springframework.stereotype.Component;

/** Delivers platform-neutral command results through OneBot. */
@Component
public class OnebotCommandResultDispatcher {
    public void dispatch(Bot bot, LazybotSlashCommandEvent event, CommandResult result) {
        if (result instanceof CommandResult.Text text) {
            CommandResultHandler.sendMessageToGroupOnebot(bot, event, text.content());
            return;
        }
        if (result instanceof CommandResult.Image image) {
            if (image.caption() == null || image.caption().isBlank()) {
                CommandResultHandler.uploadImageToOnebot(bot, event, image.content());
            }
            else {
                CommandResultHandler.sendMessageWithImageToGroupOnebot(
                        bot, event, image.content(), image.caption()
                );
            }
            return;
        }
        if (result instanceof CommandResult.Composite composite) {
            composite.results().forEach(item -> dispatch(bot, event, item));
            return;
        }
        if (result instanceof CommandResult.LegacySideEffect legacy) {
            CommandResultHandler.sendMessageToGroupOnebot(bot, event, legacy.message());
        }
    }
}
