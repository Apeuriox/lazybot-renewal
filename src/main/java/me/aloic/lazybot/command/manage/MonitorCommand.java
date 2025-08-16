package me.aloic.lazybot.command.manage;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;


@LazybotCommandMapping({"monitor"})
@Component
public class MonitorCommand implements LazybotSlashCommand
{
    @Resource
    private ManageService manageService;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        ImageUploadUtil.uploadImageToDiscord(event,manageService.commandUsage());
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        ImageUploadUtil.uploadImageToOnebot(bot,event,
                manageService.commandUsage()
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(
                manageService.commandUsage()
        );
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Command Usage Monitor","Monitor",
                        "查看Lazybot的指令使用情况",
                        "Aloic", "Aloic", "2025-07-29")
                        .addExample("/Monitor"));
    }


}
