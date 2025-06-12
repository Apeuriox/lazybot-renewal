package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
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
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        event.deferReply().queue();
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/Help.jpg");
        ImageUploadUtil.uploadImageToDiscord(event,Files.readAllBytes(Paths.get(filePath.toUri())));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/Help.jpg");
        try{
            ImageUploadUtil.uploadImageToOnebot(bot,event,Files.readAllBytes(Paths.get(filePath.toUri())));
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("[Lazybot] 读取Help页面失败");
        }

    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/Help.jpg");
        testOutputTool.saveImageToLocal(Files.readAllBytes(Paths.get(filePath.toUri())));
    }
}
