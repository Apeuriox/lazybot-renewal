package me.aloic.lazybot.command.detailedCommand;

import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@LazybotCommandMapping({"help"})
@Component
public class HelpCommand implements LazybotSlashCommand
{
    @Override
    public void executeDiscord(SlashCommandInteractionEvent event) throws Exception {
        event.deferReply().queue();
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/Help.jpg");
        ImageUploadUtil.uploadImageToDiscord(event,Files.readAllBytes(Paths.get(filePath.toUri())));
    }
}
