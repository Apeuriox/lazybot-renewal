package me.aloic.lazybot.command.manage;

import me.aloic.lazybot.command.core.CommandDefinition;
import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class HelpCommand implements PlatformIndependentCommand {
    private static final String HELP_CAPTION = "[Lazybot] 帮助页面现已合并至细分指令，输入/指令名 *h即可查询，例/card *h，进入官方群以获取更多信息，具体请看下面图片";

    @Override
    public CommandDefinition definition() {
        return CommandDefinition.discord("help", List.of(), "帮助面板指令", List.of());
    }

    @Override
    public CommandResult execute(CommandRequest request) {
        return new CommandResult.Image(loadHelpImage(), "image/jpeg", "lazybot-help.jpg", HELP_CAPTION);
    }

    @Override
    public String getHelp() {
        return "[Lazybot] 这是帮助的帮助文档";
    }

    private byte[] loadHelpImage() {
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/Help.jpg");
        try {
            return Files.readAllBytes(filePath);
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("读取Help页面失败", e);
        }
    }
}
