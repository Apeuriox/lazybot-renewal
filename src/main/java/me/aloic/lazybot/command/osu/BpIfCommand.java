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
import me.aloic.lazybot.osu.service.AnalysisService;
import me.aloic.lazybot.parameter.BpifParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@LazybotCommandMapping({"bpif"})
@Component
public class BpIfCommand implements LazybotSlashCommand
{
    @Resource
    private AnalysisService analysisService;
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
        BpifParameter params=new BpifParameter(playerName,
                OsuMode.getMode(OptionMappingTool.getOptionOrDefault(event.getOption("mode"), String.valueOf(tokenPO.getDefault_mode()))).getDescribe(),
                OptionMappingTool.getOptionOrDefault(event.getOption("operator"), "+"),
                OptionMappingTool.getOptionOrDefault(event.getOption("mods"), ""),
                OptionMappingTool.getOptionOrDefault(event.getOption("rendersize"), 30));
        params.validateParams();
        ImageUploadUtil.uploadImageToDiscord(event,analysisService.bpIf(params));
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws IOException
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        ImageUploadUtil.uploadImageToOnebot(bot,event,analysisService.bpIf(setupParameter(event,tokenPO)));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        testOutputTool.saveImageToLocal(analysisService.bpIf(setupParameter(event,tokenPO)));
    }

    private BpifParameter setupParameter(LazybotSlashCommandEvent event,AccessTokenPO tokenPO)
    {
        BpifParameter params=BpifParameter.analyzeParameter(event.getCommandParameters());
        BpifParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setPlayerId(tokenPO.getPlayer_id());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Bp If Mods","Bpif",
                        "按照指定的规则和mod重算用户的全部成绩，使用+添加mod，使用-删除mod，使用!替换mod",
                        "Aloic", "Aloic", "2024-12-07")
                        .addExample("/Bpif +HD")
                        .addExample("/Bpif Aloic -HDHR")
                        .addExample("/Bpif !HDDT")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Operator","运算符，与Mod不能有空格", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("Mod","运算的Mod，冲突的Mod只取前者", CommandParameter.ParameterType.MUST)));
    }
}
