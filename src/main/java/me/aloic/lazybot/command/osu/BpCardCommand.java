package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.RosuAlgorithmVersionUtil;
import me.aloic.lazybot.parameter.BplistParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.springframework.stereotype.Component;

@LazybotCommandMapping({"bpcard"})
@Component
public class BpCardCommand implements LazybotSlashCommand
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
        event.deferReply().queue();
        UserBindingPO tokenPO = proxy.getUserBinding(event);
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
        String playerName = OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name());
        BplistParameter params=new BplistParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("from"), 0),
                OptionMappingTool.getOptionOrDefault(event.getOption("to"), 1));
        if (event.getOption("algorithm") != null) {
            params.setAlgorithmVersion(RosuAlgorithmVersionUtil.parse(event.getOption("algorithm").getAsString()));
        }
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event,
                RendererDistributor.renderPlayerScoreListToCard(
                        playerService.bplistCardView(params), params.getFrom(), 1));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO tokenPO=proxy.getUserBinding(event);
        BplistParameter params = setupParameter(event,tokenPO);
        CommandResultHandler.uploadImageToOnebot(bot,event,
                RendererDistributor.renderPlayerScoreListToCard(
                playerService.bplistCardView(params),params.getFrom(),1)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO tokenPO=proxy.getUserBinding(event);
        BplistParameter params = setupParameter(event,tokenPO);
        testOutputTool.saveImageToLocal(
                RendererDistributor.renderPlayerScoreListToCard(
                playerService.bplistCardView(params),params.getFrom(),1)
        );
    }

    private BplistParameter setupParameter(LazybotSlashCommandEvent event, UserBindingPO tokenPO)
    {
        BplistParameter params=BplistParameter.analyzeParameter(event.getCommandParameters());
        params.applyAlgorithmVersion(event);
        BplistParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Bp List Card View","Bpcard",
                        "以指定范围查询用户的最佳成绩，以Card列表形式返回",
                        "Aloic", "Aloic", "2024-11-30")
                        .addExample("/Bpcard 1-21")
                        .addExample("/Bpcard Aloic 1-21")
                        .addExample("/Bpcard Aloic 1-21 @202502")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Range","查询的范围，[num]-[num]", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("Algorithm","以独立参数传入 @202210/@202411/@202502/@202510/@20260706；位置不限，省略时使用服务配置", CommandParameter.ParameterType.OPTIONAL)));
    }
}
