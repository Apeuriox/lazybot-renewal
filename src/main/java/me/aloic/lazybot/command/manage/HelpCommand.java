package me.aloic.lazybot.command.manage;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
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
        CommandResultHandler.uploadImageToDiscord(event,Files.readAllBytes(Paths.get(filePath.toUri())));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/Help.jpg");
        try{
            CommandResultHandler.sendMessageWithImageToGroupOnebot(bot,event,Files.readAllBytes(Paths.get(filePath.toUri())),"[Lazybot] 帮助页面现已合并至细分指令，输入/指令名 *h即可查询，例/card *h，进入官方群以获取更多信息，具体请看下面图片");
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("读取Help页面失败");
        }

    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/Help.jpg");
        testOutputTool.saveImageToLocal(Files.readAllBytes(Paths.get(filePath.toUri())));
    }
    @Override
    public String getHelp()
    {
        return "这是帮助的帮助文档";
    }
}
