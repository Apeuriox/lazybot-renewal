package me.aloic.lazybot.command.badge;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.parameter.BadgeActionParameter;
import me.aloic.lazybot.parameter.BadgeKeyParameter;
import me.aloic.lazybot.parameter.BadgeParameter;
import me.aloic.lazybot.service.BadgeKeyService;
import me.aloic.lazybot.service.BadgeService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.AuthorityVerifier;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"genkey"})
@Component
public class GenerateKeyCommand implements LazybotSlashCommand
{
    private final BadgeKeyService badgeKeyService;

    private final TestOutputTool testOutputTool;

    private final Long identity;
    private final Boolean testEnabled;

    public GenerateKeyCommand(BadgeKeyService badgeKeyService,
                              TestOutputTool testOutputTool,
                              @Value("${lazybot.test.identity}") Long identity,
                              @Value("${lazybot.test.enabled}") Boolean testEnabled)
    {
        this.badgeKeyService = badgeKeyService;
        this.testOutputTool = testOutputTool;
        this.identity = identity;
        this.testEnabled = testEnabled;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
       //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        CommandResultHandler.sendMessageToGroupOnebot(bot,event, badgeKeyService.generateKeyForCertainBadge(setupParameter(event)));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.writeStringToFile(badgeKeyService.generateKeyForCertainBadge(setupParameter(event)));
    }

    private BadgeKeyParameter setupParameter(LazybotSlashCommandEvent event)
    {
        BadgeKeyParameter params = BadgeKeyParameter.analyzeParameter(event.getCommandParameters());
        if (!testEnabled) AuthorityVerifier.isAdmin(event.getMessageEvent().getSender().getUserId());
        else AuthorityVerifier.isAdmin(identity);
        return params;
    }

    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Generate Keys","Genkey",
                        "为指定Badge生成Key",
                        "Aloic", null, "2025-10-23")
                        .code("CM-041")
                        .addExample("/Genkey 3 10 864000 true")
                        .addOption(new CommandParameter("BadgeId","Integer","Badge的ID", CommandParameter.ParameterType.REQUIRED))
                        .addOption(new CommandParameter("MaxUses","Integer","Key的最大使用次数，如果GenMultiKey为True则会生成此数量的Key", CommandParameter.ParameterType.REQUIRED))
                        .addOption(new CommandParameter("ExpireTime","Integer","过期时间，单位秒", CommandParameter.ParameterType.REQUIRED))
                        .addOption(new CommandParameter("GenMultiKey","Boolean","是否生成多个Key", CommandParameter.ParameterType.REQUIRED));
    }

}
