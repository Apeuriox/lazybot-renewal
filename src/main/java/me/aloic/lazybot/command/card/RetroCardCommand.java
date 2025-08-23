package me.aloic.lazybot.command.card;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.Service.CardService;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;


@LazybotCommandMapping({"rgc"})
@Component
public class RetroCardCommand implements LazybotSlashCommand
{
    @Resource
    private CardService cardService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {

    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO token = proxy.getAccessToken(event);
        if (event.getScorePanelVersion()==0)
        {
            ImageUploadUtil.uploadImageToOnebot(bot,event,
                    cardService.cardGameboy(token)
            );
        }
        else {
            ImageUploadUtil.uploadImageToOnebot(bot,event,
                    cardService.cardGameGadget(token)
            );
        }
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO token = proxy.getAccessToken(event);
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
