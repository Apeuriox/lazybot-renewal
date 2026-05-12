package me.aloic.lazybot.util;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.entity.message.LazybotMessageWithImage;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CommandResultHandler
{
    private static final Logger logger = LoggerFactory.getLogger(CommandResultHandler.class);
    private static final int MAX_CACHED_FILES = 3;
    private static final Queue<File> fileQueue = new LinkedList<>();


    public static void sendMessageToGroupOnebot(Bot bot, LazybotSlashCommandEvent event, String message)
    {
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(),
                MsgUtils.builder().text(message).build(),
                false);
    }



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


    public static void uploadImageToOnebot(Bot bot, LazybotSlashCommandEvent event, byte[] imageByteArray) {
            try  {
                String base64Image = Base64.getEncoder().encodeToString(imageByteArray);
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().img("base64://"+base64Image).build(), false);
            }
            catch (Exception e) {
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(),
                        MsgUtils.builder().text("发送图片失败").build(),
                        false);
                logger.error(e.getMessage());
        }
    }


    public static void sendMessageWithImageToGroupOnebot(Bot bot, LazybotSlashCommandEvent event, byte[] imageByteArray, String text) {
        try  {
            String base64Image = Base64.getEncoder().encodeToString(imageByteArray);
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(text).img("base64://"+base64Image).build(), false);
        }
        catch (Exception e) {
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(),
                    MsgUtils.builder().text("发送图片和文本失败").build(),
                    false);
            logger.error(e.getMessage());
        }
    }

    public static void sendMessageWithImageToGroupOnebot(Bot bot, LazybotSlashCommandEvent event, LazybotMessageWithImage result) {
        if (result.getImage()==null)
        {
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(result.getMessage()).build(), false);
            return;
        }
        sendMessageWithImageToGroupOnebot(bot,event, result.getImage(),result.getMessage());
    }

    public static void sendMessageWithImageToGroupOnebot(Bot bot, LazybotSlashCommandEvent event, List<LazybotMessageWithImage> result) {


        if (!CommonTool.isEmpty(result))
        {
            MsgUtils builder = MsgUtils.builder();

            for(LazybotMessageWithImage message:result)
            {
                builder.text(message.getMessage());
                if (message.getImage()!=null)
                {
                    builder.img("base64://"+Base64.getEncoder().encodeToString(message.getImage()));
                }
            }
            bot.sendGroupMsg(event.getMessageEvent().getGroupId(), builder.build(), false);
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
