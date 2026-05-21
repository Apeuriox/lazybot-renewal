package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import lombok.NonNull;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.discord.util.OptionMappingTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.TokenStarMoon;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.ManageService;
import me.aloic.lazybot.parameter.UpdateParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.CommandResultHandler;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"update"})
@Component
public class UpdateCommand implements LazybotSlashCommand
{
    @Resource
    private ManageService manageService;
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private CommandDatabaseProxy proxy;
    @Resource
    private TestOutputTool testOutputTool;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        event.deferReply().queue();
        UserTokenPO accessToken= discordTokenMapper.selectByDiscord(0L);
        UserTokenPO tokenPO = discordTokenMapper.selectByDiscord(event.getUser().getIdLong());
        if (tokenPO == null) {
            ErrorResultHandler.createNotBindOsuError(event);
            return;
        }
        tokenPO.setAccess_token(accessToken.getAccess_token());
        String playerName = OptionMappingTool.getOptionOrDefault(event.getOption("user"), tokenPO.getPlayer_name());
        UpdateParameter params=new UpdateParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("type"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        params.validateParams();
        event.getHook().sendMessage(manageService.update(params)).queue();
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO accessToken =  proxy.getAccessToken(event);
        TokenStarMoon tokenStarMoon = proxy.getStarMoonTokenIgnoreException(event);
        CommandResultHandler.sendMessageToGroupOnebot(bot,event,
                        manageService.update(
                                setupParameter(event, accessToken.getPlayer_id(), accessToken.getDefault_mode(), tokenStarMoon == null ? null : tokenStarMoon.getStar_moon_id())
                        )
                );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO accessToken =  proxy.getAccessToken(event);
        TokenStarMoon tokenStarMoon = proxy.getStarMoonTokenIgnoreException(event);
        testOutputTool.writeStringToFile(manageService.update(
                        setupParameter(event, accessToken.getPlayer_id(), accessToken.getDefault_mode(), tokenStarMoon == null ? null : tokenStarMoon.getStar_moon_id())
                )
        );
    }
    private UpdateParameter setupParameter(LazybotSlashCommandEvent event, @NonNull Integer playerId, @NonNull String mode, Integer starMoonId)
    {
        UpdateParameter params=UpdateParameter.analyzeParameter(event.getCommandParameters());
        UpdateParameter.setupDefaultValue(params,playerId,mode);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        if (starMoonId!=null)
            params.setStarMoonId(starMoonId);
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Update Cache","Update",
                        "更新用户缓存",
                        "Aloic", null, "2025-01-20")
                        .addExample("/Update Track Aloic")
                        .addExample("/Update Avatar Aloic")
                        .addExample("/Update Banner")
                        .addExample("/Update Plus")
                        .addExample("/Update PlusRecent Aloic")
                        .addOption(new CommandParameter("Type","更新的类型: avatar/track/banner/plus/plusRecent", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("PlayerName","指定的用户名称", CommandParameter.ParameterType.OPTIONAL)));
    }
}
