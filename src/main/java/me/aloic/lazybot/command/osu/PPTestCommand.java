package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//test method
@LazybotCommandMapping({"pptest"})
@Component
public class PPTestCommand implements LazybotSlashCommand
{
    private final ManageService manageService;
    private final TestOutputTool testOutputTool;
    private final CommandDatabaseProxy proxy;
    private final Long identity;


    public PPTestCommand(ManageService manageService,
                         TestOutputTool testOutputTool,
                         CommandDatabaseProxy proxy,
                         @Value("${lazybot.test.identity}") Long identity)
    {
        this.manageService = manageService;
        this.testOutputTool = testOutputTool;
        this.proxy = proxy;
        this.identity = identity;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
      return;
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
       return;
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.writeStringToFile(manageService.ppTest(ScoreCommand.setupParameter(event,proxy.getUserBinding(event)),identity));
    }

}
