package me.aloic.lazybot.command.manage;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.parameter.VerifyParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.AuthorityVerifier;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@LazybotCommandMapping({"li","linkinfo"})
public class LinkInfoCommand implements LazybotSlashCommand
{
    @Resource
    private ManageService manageService;
    @Value("${lazybot.test.identity}")
    private Long identity;
    @Resource
    private TestOutputTool testOutputTool;
    @Resource
    private CommandDatabaseProxy proxy;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {

    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AuthorityVerifier.isAdmin(event.getMessageEvent().getSender().getUserId());
        long targetCode;
        try{
            targetCode = Long.parseLong(event.getCommandParameters().getFirst());
        }
        catch (Exception e) {
            throw new RuntimeException("输入不合法或为空");
        }
        UserBindingPO token=proxy.getQqBinding(targetCode, true);
        if (token==null) CommandResultHandler.sendMessageToGroupOnebot(bot,event,"该用户暂未绑定");
        else CommandResultHandler.sendMessageToGroupOnebot(bot,event,token.toSimpleString());
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AuthorityVerifier.isAdmin(identity);
        long targetCode;
        try{
            targetCode = Long.parseLong(event.getCommandParameters().getFirst());
        }
        catch (Exception e) {
            throw new RuntimeException("输入不合法或为空");
        }
        UserBindingPO token=proxy.getQqBinding(targetCode, true);
        if (token==null)  testOutputTool.writeStringToFile("该用户暂未绑定");
        else testOutputTool.writeStringToFile(token.toSimpleString());
    }
}
