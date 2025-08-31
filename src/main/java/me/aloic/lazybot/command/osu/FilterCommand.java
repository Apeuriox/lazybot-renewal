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
import me.aloic.lazybot.osu.filter.ScoreFilter;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.BpParameter;
import me.aloic.lazybot.parameter.ScoreFilterParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@LazybotCommandMapping({"f","filter"})
@Component
public class FilterCommand implements LazybotSlashCommand
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
       //not now
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        ImageUploadUtil.uploadImageToOnebot(bot,event,playerService.bpScoreFilter(setupParameter(event,tokenPO)));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        testOutputTool.saveImageToLocal(playerService.bpScoreFilter(setupParameter(event,tokenPO)));
    }

    private ScoreFilterParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO)
    {
        ScoreFilterParameter params=ScoreFilterParameter.analyzeParameter(event.getCommandParameters());
        ScoreFilterParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Filter","Filter, F",
                        "【测试阶段】以指定的条件过滤用户的BP 200成绩，大小写不敏感，参数结构为 [字段][运算符][预期值]，分隔符号为半角逗号，最大渲染数量为50",
                        "Aloic", "Aloic", "2025-08-31")
                        .addExample("/Filter Star>7")
                        .addExample("/F Aloic Star>7, Bpm<230, Title^=I")
                        .addOption(new CommandParameter("Statement", """
                                过滤的条件，支持的条件为：
                                Accuracy, acc: 准确率
                                Artist: 曲师
                                AR: 谱面的缩圈速度
                                BPM： 曲目的每分钟节拍
                                Combo： 成绩的最大Combo
                                Circle: 谱面的Note数量
                                Creator, Mapper: 谱师
                                Difficulty, Diff, Version: 谱面的难度
                                Great, 300： 成绩的300数量
                                HP： 谱面的掉血速度
                                Length: 谱面的秒数
                                Meh, 50： 成绩的50数量
                                MaxCombo: 谱面的最大Combo数
                                Mod, Mods： 成绩的启用模组
                                Miss, 0： 成绩的Miss数量
                                OK, 100: 成绩的100数
                                OD： 谱面的OD
                                PP，Performance： 成绩的表现点
                                Rank: 成绩的评级，采用Lazer算法
                                Slider： 谱面的滑条数量
                                Spinner： 谱面的转盘数量
                                Star： 谱面的难度星级
                                Title, Name： 谱面标题""", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Operator", """
                                过滤的运算符，支持的运算符号为：
                                数字: >  大于
                                     >=  大于等于
                                     <  小于
                                     <=  小于等于
                                     ==或=  相等
                                     !=  不相等
                                文本: ==  完全相等
                                     ^=  以...开头
                                     $=  以...结尾
                                     ~  相似
                                     =  包含
                                     !=  不相等
                                模组: =  包含
                                     == 完全相等""", CommandParameter.ParameterType.OPTIONAL)));
    }

}
