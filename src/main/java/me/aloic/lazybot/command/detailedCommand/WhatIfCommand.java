package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.TipsParameter;
import me.aloic.lazybot.parameter.WhatIfParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"whatif"})
@Component
public class WhatIfCommand implements LazybotSlashCommand
{
    @Resource
    private FunService funService;
    @Resource
    private TestOutputTool testOutputTool;
    @Resource
    private CommandDatabaseProxy proxy;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        WhatIfParameter params=new WhatIfParameter();
        params.validateParams();
        event.getHook().sendMessage(funService.whatIfIGotSomePP(params)).queue();
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        WhatIfParameter params=WhatIfParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        WhatIfParameter.setupDefaultValue(params,proxy.getAccessToken(event));
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(funService.whatIfIGotSomePP(params)).build(),false);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        WhatIfParameter params= WhatIfParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        WhatIfParameter.setupDefaultValue(params,proxy.getAccessToken(event));
        testOutputTool.writeStringToFile(funService.whatIfIGotSomePP(params));
    }
}
