package me.aloic.lazybot.command.manage;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.MonthDay;


@LazybotCommandMapping({"年度总结"})
@Component
public class AnnualSummaryCommand implements LazybotSlashCommand
{
    @Resource
    private ManageService manageService;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
       //
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        if (isInTimeRange())
        {
            CommandResultHandler.sendMessageToGroupOnebot(bot,event,
                    manageService.annualCommandUsage()
            );
        }
        else {
            CommandResultHandler.sendMessageToGroupOnebot(bot,event,
                    "当前不在统计时间范围内，请于12月14日至1月16日之间查看"
            );
        }

    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        if (isInTimeRange())
            testOutputTool.writeStringToFile(
                    manageService.annualCommandUsage()
            );
        else {
            testOutputTool.writeStringToFile(
                    "当前不在统计时间范围内，请于12月14日至1月16日之间查看"
            );
        }
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Annual Command Usage Summary","年度总结",
                        "查看Lazybot的年度指令使用总结",
                        "Aloic", null, "2025-12-18")
                        .addExample("/年度总结"));
    }

    private boolean isInTimeRange()
    {
        MonthDay now = MonthDay.from(LocalDate.now());

        return now.isAfter(MonthDay.of(12, 14))
                        || now.isBefore(MonthDay.of(1, 16));
    }


}
