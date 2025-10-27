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
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"nochoke","nc","no1miss"})
@Component
public class NoChokeCommand implements LazybotSlashCommand
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
        if (event.getFullCommandName().equalsIgnoreCase("no1miss"))
            CommandResultHandler.uploadImageToDiscord(event,playerService.noChoke(params,1));
        else CommandResultHandler.uploadImageToDiscord(event,playerService.noChoke(params,0));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        if (event.getCommandType().equalsIgnoreCase("no1miss"))
            CommandResultHandler.uploadImageToOnebot(bot,event,
                    playerService.noChoke(
                            GeneralParameter.setupParameter(event, proxy.getAccessToken(event)), 1)
            );
        else  CommandResultHandler.uploadImageToOnebot(bot,event,
                playerService.noChoke(
                        GeneralParameter.setupParameter(event, proxy.getAccessToken(event)), 0)
        );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        if (event.getCommandType().equalsIgnoreCase("no1miss"))
            testOutputTool.saveImageToLocal(
                    playerService.noChoke(
                            GeneralParameter.setupParameter(event, proxy.getAccessToken(event)), 1)
            );
        else testOutputTool.saveImageToLocal(
                playerService.noChoke(
                        GeneralParameter.setupParameter(event, proxy.getAccessToken(event)), 0)
        );
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("No Choke","NoChoke, nc, no1miss",
                        "以FC计算用户的全部Bp，使用no1miss仅计算<=1miss的成绩",
                        "Aloic", "Aloic", "2024-05-20")
                        .addExample("/NoChoke")
                        .addExample("/NoChoke Aloic")
                        .addExample("/No1Miss Aloic")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL)));
    }
}
