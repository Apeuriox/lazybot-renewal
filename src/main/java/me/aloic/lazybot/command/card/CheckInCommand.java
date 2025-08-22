package me.aloic.lazybot.command.card;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.Service.CardService;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

@LazybotCommandMapping({"check","checkin","ci"})
@Component
public class CheckInCommand implements LazybotSlashCommand
{
    @Resource
    private CardService cardService;
    @Resource
    private TestOutputTool testOutputTool;
    @Resource
    private CommandDatabaseProxy proxy;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        AccessTokenPO token =  proxy.getAccessToken(event);
        if (event.getScorePanelVersion() == 0)
        {
            ImageUploadUtil.uploadImageToOnebot(bot, event, cardService.checkIn(token));
        }
        else bot.sendGroupMsg(event.getMessageEvent().getGroupId(), cardService.checkIn(token.getPlayer_id()),false);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO token =  proxy.getAccessToken(event);
        if (event.getScorePanelVersion() == 0)
        {
            testOutputTool.saveImageToLocal(cardService.checkIn(token));
        }
        else testOutputTool.writeStringToFile(cardService.checkIn(token.getPlayer_id()));

    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Check In","CheckIn, Check, CI", "签到获取LazyCoin","Aloic", "Aloic", "2025-08-21")
                .addExample("/checkin")
                .addExample("/ci"));
    }
}
