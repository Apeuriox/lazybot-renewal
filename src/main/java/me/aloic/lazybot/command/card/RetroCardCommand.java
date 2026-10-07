package me.aloic.lazybot.command.card;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.service.CardService;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;


@LazybotCommandMapping({"rgc"})
@Component
public class RetroCardCommand implements LazybotSlashCommand
{
    private final CardService cardService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;

    public RetroCardCommand(CardService cardService,
                            CommandDatabaseProxy proxy,
                            TestOutputTool testOutputTool)
    {
        this.cardService = cardService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {

    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO token = proxy.getUserBinding(event);
        if (event.getScorePanelVersion()==0)
        {
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    cardService.cardGameboy(token)
            );
        }
        else {
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    cardService.cardGameGadget(token)
            );
        }
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO token = proxy.getUserBinding(event);
        if (event.getScorePanelVersion()==0)
        {
            testOutputTool.saveImageToLocal(
                    cardService.cardGameboy(token)
            );
        }
        else {
            testOutputTool.saveImageToLocal(
                    cardService.cardGameGadget(token)
            );
        }
    }

    @Override
    public String getHelp()
    {
        return LazybotSlashCommand.super.getHelp();
    }
}
