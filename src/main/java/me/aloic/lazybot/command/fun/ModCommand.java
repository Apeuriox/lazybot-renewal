package me.aloic.lazybot.command.fun;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import me.aloic.lazybot.entity.CommandSummary;
import java.util.List;

@LazybotCommandMapping({"mod","modinfo","mi"})
@Component
public class ModCommand implements LazybotSlashCommand
{
    private final FunService funService;
    private final TestOutputTool testOutputTool;
    public ModCommand(FunService funService,
                      TestOutputTool testOutputTool)
    {
        this.funService = funService;
        this.testOutputTool = testOutputTool;
    }

    private static final CommandSummary SUMMARY = new CommandSummary(
            CommandSummary.Category.OSU,
            "ModInfo",
            List.of("mod"),
            "{modName}",
            "/mod HR",
            "查询对应 Mod 的介绍",
            "还没做完");
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        GeneralParameter params=new GeneralParameter(OptionMappingTool.getOptionOrDefault(event.getOption("id"),"null"),null);
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event, Files.readAllBytes(Paths.get(funService.modInfo(params).toUri())));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event)
    {
        GeneralParameter params=GeneralParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        try{
            CommandResultHandler.uploadImageToOnebot(bot,event, Files.readAllBytes(Paths.get(funService.modInfo(params).toUri())));
        }
        catch (Exception e){
            throw new LazybotRuntimeException("要么你输入的Mod名有问题，要么此Mod的页面还未创建");
        }

    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        GeneralParameter params=GeneralParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        try{
            testOutputTool.saveImageToLocal(Files.readAllBytes(Paths.get(funService.modInfo(params).toUri())));
        }
        catch (Exception e){
            throw new LazybotRuntimeException("要么你输入的Mod名有问题，要么此Mod的页面还未创建");
        }

    }
    @Override
    public CommandHelp commandHelp()
    {
        return new CommandHelp("Mod Info","Mod, modInfo, mi", "查看Osu!下指定Mod的信息","Aloic", "Aloic", "2025-05-09")
                .code("CM-023")
                .headline("See Mod Wiki")
                .availability("ALL MODE")
                .addExample("/Mod Hidden")
                .addExample("/Mod HD")
                .addOption(new CommandParameter("Mod名称","String","Mod的名称，支持全称及缩写", CommandParameter.ParameterType.REQUIRED));
    }
}
