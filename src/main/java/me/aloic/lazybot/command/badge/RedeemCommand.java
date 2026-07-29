package me.aloic.lazybot.command.badge;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.parameter.BadgeKeyParameter;
import me.aloic.lazybot.service.BadgeKeyService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.AuthorityVerifier;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"redeem"})
@Component
public class RedeemCommand implements LazybotSlashCommand
{
    @Resource
    private BadgeKeyService badgeKeyService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    private static final String REGEX_KEY_FORMAT;

    static{
        REGEX_KEY_FORMAT = "^[A-Z0-9]{4}(-[A-Z0-9]{4}){4}$";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
       //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO token = proxy.getUserBinding(event);
        if (event.getCommandParameters()==null || event.getCommandParameters().isEmpty())
        {
            throw new IllegalArgumentException("未检测到Key");
        }
        String key = event.getCommandParameters().getFirst();
        if(key != null && key.matches(REGEX_KEY_FORMAT))
            CommandResultHandler.sendMessageToGroupOnebot(bot,event, badgeKeyService.redeemBadge(token.getId(),key));
        else throw new IllegalArgumentException("Key格式不正确");
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO token = proxy.getUserBinding(event);
        if (event.getCommandParameters()==null || event.getCommandParameters().isEmpty())
        {
            throw new IllegalArgumentException("未检测到Key");
        }
        String key = event.getCommandParameters().getFirst();
        if(key != null && key.matches(REGEX_KEY_FORMAT))
            testOutputTool.writeStringToFile(badgeKeyService.redeemBadge(token.getId(),key));
        else
        {
            throw new IllegalArgumentException("Key格式不正确");
        }
    }


    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Redeem","Redeem",
                        "使用Key兑换Badge",
                        "Aloic", null, "2025-10-23")
                        .addExample("/Redeem AAAA-BBBB-CCCC-DDDD-EEEE")
                        .addOption(new CommandParameter("Key","Key，格式为AAAA-BBBB-CCCC-DDDD-EEEE", CommandParameter.ParameterType.MUST)));
    }

}
