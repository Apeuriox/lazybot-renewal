package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.ScoreParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@LazybotCommandMapping({"rank","sr","scorerank"})
@Component
public class ScoreRankCommand implements LazybotSlashCommand
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
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception {
        Long groupId = event.getMessageEvent().getGroupId();
        if(groupId == null) {
            return;
        }
        // 先查询当前群组的名单
        List<GroupMemberInfoResp> members = bot.getGroupMemberList(groupId).getData();
        if(CollectionUtils.isEmpty(members)) {
            return;
        }
        ScoreParameter scoreParameter = ScoreCommand.setupParameter(event, proxy.getAccessToken(event));
        scoreParameter.setGroupUserIds(members.stream().map(GroupMemberInfoResp::getUserId).collect(Collectors.toList()));
        ImageUploadUtil.uploadImageToOnebot(bot,event, playerService.scoreRank(scoreParameter));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception {
        // only support in group
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("All Score","AllScore, AllScores, As, Ass",
                        "查询对应玩家在对应地图下的全部成绩，以及查询部分pp计算中间值",
                        "Aloic", "Aloic", "2025-06-03")
                        .addExample("/Allscore 4889657")
                        .addExample("/Allscore Aloic 4889657")
                        .addOption(new CommandParameter("PlayerName","查询的玩家名称", CommandParameter.ParameterType.OPTIONAL))
                        .addOption(new CommandParameter("Bid","地图ID", CommandParameter.ParameterType.MUST)));
    }

}
