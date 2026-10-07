package me.aloic.lazybot.command.card;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.service.CardService;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"check","checkin","ci"})
@Component
public class CheckInCommand implements LazybotSlashCommand
{
    private final CardService cardService;
    private final TestOutputTool testOutputTool;
    private final CommandDatabaseProxy proxy;

    public CheckInCommand(CardService cardService,
                          TestOutputTool testOutputTool,
                          CommandDatabaseProxy proxy)
    {
        this.cardService = cardService;
        this.testOutputTool = testOutputTool;
        this.proxy = proxy;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        UserBindingPO token =  proxy.getUserBinding(event);
        if (event.getScorePanelVersion() == 0)
        {
            CommandResultHandler.uploadImageToOnebot(bot, event, cardService.checkIn(token));
        }
        else bot.sendGroupMsg(event.getMessageEvent().getGroupId(), cardService.checkIn(token.getPlayer_id()),false);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO token =  proxy.getUserBinding(event);
        if (event.getScorePanelVersion() == 0) {
            testOutputTool.saveImageToLocal(cardService.checkIn(token));
        }
        else testOutputTool.writeStringToFile(cardService.checkIn(token.getPlayer_id()));
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Check In","CheckIn, Check, CI", "签到获取LazyCoin","Aloic", "Aloic", "2025-08-21")
                .code("CM-031")
                .headline("Check In")
                .availability("NOT OSU")
                .addExample("/checkin")
                .addExample("/ci");
    }
}
