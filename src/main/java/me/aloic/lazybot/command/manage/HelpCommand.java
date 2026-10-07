package me.aloic.lazybot.command.manage;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.annotation.SkipLazybotCommandPreprocessing;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.command.registry.LazybotSlashCommandRegistry;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.graphics.service.RasterizationService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

@LazybotCommandMapping({"help"})
@SkipLazybotCommandPreprocessing
@Component
public class HelpCommand implements LazybotSlashCommand
{
    private final TestOutputTool testOutputTool;
    private final LazybotSlashCommandRegistry commandRegistry;
    private final RasterizationService rasterizationService;

    public HelpCommand(TestOutputTool testOutputTool,
                       LazybotSlashCommandRegistry commandRegistry,
                       RasterizationService rasterizationService)
    {
        this.testOutputTool = testOutputTool;
        this.commandRegistry = commandRegistry;
        this.rasterizationService = rasterizationService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        event.deferReply().queue();
        CommandResultHandler.uploadImageToDiscord(event, render());
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        CommandResultHandler.sendMessageWithImageToGroupOnebot(bot, event, render(),
                "[Lazybot] 输入 /指令名 *h 查看单条帮助，例如 /card *h");
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(render());
    }

    private byte[] render()
    {
        return rasterizationService.renderHelpIndex(commandRegistry.commands().stream()
                .flatMap(command -> command.getCommandSummaries().stream())
                .filter(Objects::nonNull)
                .toList());
    }
    @Override
    public String getHelp()
    {
        return "[Lazybot] 这是帮助的帮助文档";
    }
}
