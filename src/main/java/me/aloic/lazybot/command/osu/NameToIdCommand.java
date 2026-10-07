package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.NameToIdParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.aloic.lazybot.entity.CommandSummary;
@Component
@LazybotCommandMapping({"nametoid","n2d"})
public class NameToIdCommand implements LazybotSlashCommand
{
    private final PlayerService playerService;
    private final CommandDatabaseProxy proxy;
    private final TestOutputTool testOutputTool;

    public NameToIdCommand(PlayerService playerService,
                           CommandDatabaseProxy proxy,
                           TestOutputTool testOutputTool)
    {
        this.playerService = playerService;
        this.proxy = proxy;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "NameToId",
            List.of("n2d"),
            "{userNameArray}",
            "/nameToId Aloic,ATRI1024,Zh_Jk",
            "将指定的用户名序列转化为 UID",
            "间隔符为`,`，与其他指令不同");

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserBindingPO tokenPO = proxy.getUserBinding(event);
        String playerNameList = OptionMappingTool.getOptionOrDefault(event.getOption("list"), tokenPO.getPlayer_name());
        List<String> playerNames = Arrays.stream(playerNameList.split(","))
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
        NameToIdParameter params=new NameToIdParameter(playerNames,"osu");
        params.validateParams();
        event.getHook().sendMessage(playerService.nameToId(params)).queue();
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        UserBindingPO accessToken = proxy.getUserBinding(event);
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(),
                MsgUtils.builder().text(
                        playerService.nameToId(
                                setupParameter(event,accessToken))
                ).build(),false);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.writeStringToFile(
                playerService.nameToId(
                        setupParameter(event, proxy.getUserBinding(event))
                )
        );
    }
    private NameToIdParameter setupParameter(LazybotSlashCommandEvent event,UserBindingPO tokenPO)
    {
        NameToIdParameter params=NameToIdParameter.analyzeParameter(event.getCommandParameters());
        NameToIdParameter.setupDefaultValue(params,tokenPO);
        params.validateParams();
        return params;
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Name to ID","n2d, nametoid",
                        "接受一段连续的用户名输入，返回对应的osu id，分割符为,",
                        "Aloic", null, "2025-01-07")
                .code("CM-016")
                .headline("Transform to IDs")
                .availability("ALL MODE")
                        .addExample("/n2d Aloic,Pager,Hidden is fun,Zh_jk")
                        .addOption(new CommandParameter("PlayerNameList","Custom","查询的玩家名称列表", CommandParameter.ParameterType.REQUIRED));
    }
}
