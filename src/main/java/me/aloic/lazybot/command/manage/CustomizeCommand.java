package me.aloic.lazybot.command.manage;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.annotation.SkipLazybotCommandPreprocessing;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.CustomizeService;
import me.aloic.lazybot.parameter.CustomizationParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@LazybotCommandMapping({"customize"})
@SkipLazybotCommandPreprocessing
@Component
public class CustomizeCommand implements LazybotSlashCommand
{
    @Resource
    private CustomizeService customizeService;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    private static final CommandSummary PROFILE_BG = new CommandSummary(
            CommandSummary.Category.CUSTOMIZE,
            "Customize",
            List.of(),
            "{URL}",
            "/customize profilebg https://this.is.link",
            "更改 /profile 的背景图片，URL为图片链接，提交后需要等待验证",
            "图片需要为 1900x1000，超出的部分会被裁剪",
            "profileBG");

    private static final CommandSummary PROFILE_THEME = new CommandSummary(
            CommandSummary.Category.CUSTOMIZE,
            "Customize",
            List.of(),
            "{Light 或 Dark 或 Lighter}",
            "/customize profileTheme Light",
            "更改 /profile 的颜色预设",
            "",
            "profileTheme");
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
        CustomizationParameter params=new CustomizationParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("type"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        params.validateParams();
        event.getHook().sendMessage(customizeService.customize(params)).queue();
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(),
                MsgUtils.builder().text(
                        customizeService.customize(
                                setupParameter(event,
                                        proxy.getUserBinding(event))
                        )
                ).build(),false);
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.writeStringToFile(
                customizeService.customize(
                        setupParameter(event,
                                proxy.getUserBinding(event))
                )
        );
    }
    private CustomizationParameter setupParameter(LazybotSlashCommandEvent event,UserBindingPO tokenPO)
    {
        CustomizationParameter params=CustomizationParameter.analyzeParameter(event.getCommandParameters());
        CustomizationParameter.setupDefaultValue(params,tokenPO);
        params.validateParams();
        return params;
    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Customize","Customize",
                        "自定义命令，用于修改/Info中的背景和主题",
                        "Aloic", null, "2025-02-19")
                .code("CM-022")
                .headline("Customize Your Profile Banner")
                .availability("NOT OSU")
                        .addExample("/Customize profileBG https://this.is.link")
                        .addExample("/Customize profileTheme Light")
                        .addOption(new CommandParameter("Type","String","二级命令类型，profileBG修改地图背景，profileTheme修改主题", CommandParameter.ParameterType.REQUIRED))
                        .addOption(new CommandParameter("BGLink","String","仅限profileBG，背景的链接，接受输入1900x1000，多余部分会被裁剪", CommandParameter.ParameterType.REQUIRED))
                        .addOption(new CommandParameter("Theme","String","仅限profileTheme，更改其的颜色预设，支持输入Light, Lighter, Dark", CommandParameter.ParameterType.REQUIRED));
    }

    @Override
    public java.util.List<CommandSummary> getCommandSummaries()
    {
        return java.util.List.of(PROFILE_BG, PROFILE_THEME);
    }

}
