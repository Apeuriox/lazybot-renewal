package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

@LazybotCommandMapping({"mod","modinfo","mi"})
@Component
public class ModCommand implements LazybotSlashCommand
{
    @Resource
    private FunService funService;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        GeneralParameter params=new GeneralParameter(OptionMappingTool.getOptionOrDefault(event.getOption("id"),"null"),null);
        params.validateParams();
        ImageUploadUtil.uploadImageToDiscord(event, Files.readAllBytes(Paths.get(funService.modInfo(params).toUri())));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        GeneralParameter params=GeneralParameter.analyzeParameter(event.getCommandParameters());
        params.validateParams();
        try{
            ImageUploadUtil.uploadImageToOnebot(bot,event, Files.readAllBytes(Paths.get(funService.modInfo(params).toUri())));
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
}
