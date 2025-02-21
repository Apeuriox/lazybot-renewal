package me.aloic.lazybot.util;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.ibatis.io.ExternalResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class ImageUploadUtil
{
    private static final Logger logger = LoggerFactory.getLogger(ImageUploadUtil.class);
    private static final int MAX_CACHED_FILES = 3;
    private static final Queue<File> fileQueue = new LinkedList<>();

    public static void uploadImageToDiscord(SlashCommandInteractionEvent event, byte[] imageByteArray)
    {
        if (imageByteArray != null) {
            logger.info("Image size: {}", imageByteArray.length);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageByteArray);
            FileUpload fileUpload = FileUpload.fromData(inputStream, "lazybot-image.png");
            event.getHook().sendFiles(fileUpload).queue();
        } else {
            event.getHook().sendMessage("Failed to render image.").setEphemeral(true).queue();
        }
    }

    private static File saveBytesToFile(byte[] imageByteArray, String fileName) throws IOException
    {
        long startTime = System.currentTimeMillis();
        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(imageByteArray);
        }
        logger.info("Saving image to file cost: {}ms", System.currentTimeMillis() - startTime);
        return tempFile;
    }
    public static void uploadImageToOnebot(Bot bot, LazybotSlashCommandEvent event, byte[] imageByteArray) {
        try {
            File tempFile = saveBytesToFile(imageByteArray, String.valueOf(event.getMessageEvent().getSender().getUserId()));
            try  {
                String filePath = "file:///".concat(tempFile.getAbsolutePath());
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().img(filePath).build(), false);
            }
            finally {
                tempFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void uploadImageToOnebot(Bot bot, LazybotSlashCommandEvent event, String filePath) {
        try {
            filePath = "file:///".concat(filePath);
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().img(filePath).build(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static File saveBytesToFile(byte[] imageBytes, String fileName) throws IOException {
//        long startTime = System.currentTimeMillis();
//        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
//
//        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//            fos.write(imageBytes);
//        }
//        synchronized (fileQueue) {
//            fileQueue.add(tempFile);
//            while (fileQueue.size() > MAX_CACHED_FILES) {
//                File oldestFile = fileQueue.poll();
//                if (oldestFile != null && oldestFile.exists()) {
//                    oldestFile.delete();
//                }
//            }
//        }
//        logger.info("Saving image to file cost: {}ms", System.currentTimeMillis() - startTime);
//        return tempFile;
//    }
}
