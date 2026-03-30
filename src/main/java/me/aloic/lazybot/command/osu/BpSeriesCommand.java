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
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.BplistParameter;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.SeriesParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@LazybotCommandMapping({"bps","bs","bssm"})
@Component
public class BpSeriesCommand implements LazybotSlashCommand
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
        BplistParameter params=new BplistParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                1,21);
        params.validateParams();
        CommandResultHandler.uploadImageToDiscord(event, RendererDistributor.renderPlayerScoreListToCard(playerService.bplistCardView(params),params.getFrom(),1));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        GeneralParameter parameter=GeneralParameter.setupParameter(event,tokenPO);
        BplistParameter params=new BplistParameter(parameter.getPlayerId(),
                parameter.getMode(),
                1,21);
        if (parameter.getPlayerName()!=null) params.setPlayerName(parameter.getPlayerName());
        if (event.getScorePanelVersion()==0)
                CommandResultHandler.uploadImageToOnebot(bot,event,
                        RendererDistributor.renderPlayerScoreListToCard(playerService.bplistCardView(params),params.getFrom(),1));
        else
                CommandResultHandler.uploadImageToOnebot(bot,event,
                        RendererDistributor.renderPlayerScoreListToList(playerService.bplistListView(params), params.getFrom())
                );
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        SeriesParameter parameter=SeriesParameter.setupParameter(event,tokenPO.getPlayer_id(), tokenPO.getDefault_mode());
        BplistParameter params=new BplistParameter(parameter.getPlayerName(),
                parameter.getPlayerId(),
                parameter.getMode(),
                1, parameter.getMaxIndex());
        if (parameter.getPlayerName()!=null) params.setPlayerName(parameter.getPlayerName());
        if (event.getScorePanelVersion()==0)
            testOutputTool.saveImageToLocal(RendererDistributor.renderPlayerScoreListToCard(playerService.bplistCardView(params),params.getFrom(),1));
        else
            testOutputTool.saveImageToLocal(RendererDistributor.renderPlayerScoreListToList(playerService.bplistListView(params), params.getFrom())
            );
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Bp Series","Bs, Bps",
                        "等效/Bpcard [start]-[index]，用于快速查询，输入&以List形式返回，默认为21",
                        "Aloic", "Aloic", "2024-07-23")
                        .addExample("/Bs")
                        .addExample("/Bs Aloic")
                        .addExample("/Bs Aloic 31 &")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Index","最大查询范围，默认21", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Version","存在&则以List形式输出", CommandParameter.ParameterType.OPTIONAL)));
    }


}
