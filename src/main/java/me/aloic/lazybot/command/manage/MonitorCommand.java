package me.aloic.lazybot.command.manage;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;


@LazybotCommandMapping({"monitor"})
@Component
public class MonitorCommand implements LazybotSlashCommand
{
    private final ManageService manageService;
    private final TestOutputTool testOutputTool;

    public MonitorCommand(ManageService manageService,
                          TestOutputTool testOutputTool)
    {
        this.manageService = manageService;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.FUN,
            "Monitor",
            List.of(),
            "",
            "/monitor",
            "查询 bot 的指令使用情况",
            "");
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        CommandResultHandler.uploadImageToDiscord(event, RendererDistributor.renderCommandUsage(
                manageService.commandUsage()
        ));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderCommandUsage(
                        manageService.commandUsage()
                )
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderCommandUsage(
                        manageService.commandUsage()
                )
        );
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Command Usage Monitor","Monitor",
                        "查看Lazybot的指令使用情况",
                        "Aloic", "Aloic", "2025-07-29")
                .code("CM-028")
                .headline("Lazybot Command Usage")
                .availability("NOT OSU")
                        .addExample("/Monitor");
    }


}
