package me.aloic.lazybot.util;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;

public class ImageUploadUtil
{
    private static final Logger logger = LoggerFactory.getLogger(ImageUploadUtil.class);
    public static void uploadImageToDiscord(SlashCommandInteractionEvent event, byte[] imageByteArray)
    {
        if (imageByteArray != null) {
            logger.info("Image size: {}", imageByteArray.length);
            long startingTime = System.currentTimeMillis();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageByteArray);
            FileUpload fileUpload = FileUpload.fromData(inputStream, "lazybot-image.png");
            event.getHook().sendFiles(fileUpload).queue();
            logger.info("Upload cost:{}ms", System.currentTimeMillis() - startingTime);
        } else {
            event.getHook().sendMessage("Failed to render image.").setEphemeral(true).queue();
        }
    }
}
