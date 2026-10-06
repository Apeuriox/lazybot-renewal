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
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.TrackService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@Component
@LazybotCommandMapping({"ppmap"})
public class PpmapCommand implements LazybotSlashCommand
{
    @Resource
    private TrackService trackService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "PPmap",
            List.of(),
            "[userName]",
            "/ppmap Aloic",
            "绘制指定用户的历史BP",
            "数据来源为 OsuTrack，此功能并不是绘制你 BP 100");
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
        GeneralParameter params=new GeneralParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event, trackService.ppTimeMap(params));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        CommandResultHandler.uploadImageToOnebot(bot,event,
                trackService.ppTimeMap(
                        setupParameter(event,
                                proxy.getUserBinding(event))
                )
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(trackService.ppTimeMap(
                        setupParameter(event,
                                proxy.getUserBinding(event))
                )
        );
    }
    private GeneralParameter setupParameter(LazybotSlashCommandEvent event,UserBindingPO tokenPO)
    {
        GeneralParameter params=GeneralParameter.analyzeParameter(event.getCommandParameters());
        GeneralParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Time-PP Scatter Chart","ppmap",
                        "以Osu Track数据绘制指定用户的历史BP散点图，使用/update track可更新数据",
                        "Aloic", null, "2024-06-14")
                .code("CM-011")
                .headline("Scatter Map of History Bps")
                .availability("ALL MODE")
                        .addExample("/ppmap")
                        .addExample("/ppmap Aloic")
                        .addOption(new CommandParameter("PlayerName","String","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL));
    }
}
