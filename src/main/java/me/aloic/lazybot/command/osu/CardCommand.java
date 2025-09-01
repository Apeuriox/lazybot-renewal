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
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@LazybotCommandMapping({"card"})
public class CardCommand implements LazybotSlashCommand
{
    @Resource
    private PlayerService playerService;
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
        GeneralParameter params=new GeneralParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe());
        params.validateParams();
        ImageUploadUtil.uploadImageToDiscord(event,playerService.card(params));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        if (event.getScorePanelVersion()==0)
            ImageUploadUtil.uploadImageToOnebot(bot,event,
                    playerService.card(
                            setupParameter(event, proxy.getAccessToken(event))
                    )
            );
        else
        {
            ImageUploadUtil.uploadImageToOnebot(bot,event,playerService.cardMoelleux(
                            setupParameter(event, proxy.getAccessToken(event))
                    ));
        }
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        if (event.getScorePanelVersion()==0)
            testOutputTool.saveImageToLocal(
                    playerService.card(
                            setupParameter(event, proxy.getAccessToken(event))
                    )
            );
        else
            testOutputTool.saveImageToLocal(
                    playerService.cardMoelleux(
                            setupParameter(event, proxy.getAccessToken(event))
                    )
            );
    }
    private GeneralParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO)
    {
        GeneralParameter params=GeneralParameter.analyzeParameter(event.getCommandParameters());
        GeneralParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Card","Card",
                        "查询个人资料, 生成小型卡片样式",
                        "Aloic", "Aloic", "2024-03-22 (原版) / 2025-08-05 (Moelleux样式)")
                        .addExample("/Card")
                        .addExample("/Card Aloic")
                        .addExample("/Card &")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","存在&则以Moelleux样式输出，需要有足够权限", CommandParameter.ParameterType.OPTIONAL)));
    }
}
