package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.PlusListParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"pbpcard","pb","pbplist"})
@Component
public class PlusBpCardCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
    @Resource
    private CommandDatabaseProxy proxy;
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
        UserBindingPO tokenPO=proxy.getUserBinding(event);
        PlusListParameter params = setupParameter(event,tokenPO);
        CommandResultHandler.sendMessageWithImageToGroupOnebot(bot,event,
                RendererDistributor.renderPlusScoresToCardList(
                playerService.getPerformanceDimensionList(params)
                ),"下图为您的PP+最好成绩，和ppy的算法不一样请注意"
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO tokenPO=proxy.getUserBinding(event);
        PlusListParameter params = setupParameter(event,tokenPO);
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderPlusScoresToCardList(
                        playerService.getPerformanceDimensionList(params)
                )
        );
    }

    private PlusListParameter setupParameter(LazybotSlashCommandEvent event, UserBindingPO tokenPO)
    {
        PlusListParameter params=PlusListParameter.analyzeParameter(event.getCommandParameters());
        PlusListParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Plus Bp List Card View","Pbpcard, Pb",
                        "以指定范围查询用户的PP+最佳成绩，以Card列表形式返回",
                        "Aloic", "Aloic", "2026-03-24")
                        .addExample("/Pb 1-21")
                        .addExample("/Pb Aloic 1-21")
                        .addExample("/Pb Aloic 1-21 flow")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Range","查询的范围，[num]-[num]", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("Dimension","查询的维度", CommandParameter.ParameterType.OPTIONAL)));
    }
}
