package me.aloic.lazybot.command.detailedCommand;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.CustomizationMapper;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.ProfileParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.DataObjectExtractor;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@LazybotCommandMapping({"ppp","plus"})
public class PerformancePlusCommand implements LazybotSlashCommand
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

    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        ImageUploadUtil.uploadImageToOnebot(bot,event,
                playerService.performancePlus(
                        GeneralParameter.setupParameter(event,
                                proxy.getAccessToken(event))
                )
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        testOutputTool.saveImageToLocal(playerService.performancePlus(
                        GeneralParameter.setupParameter(event,
                                proxy.getAccessToken(event))
                )
        );
    }


}
