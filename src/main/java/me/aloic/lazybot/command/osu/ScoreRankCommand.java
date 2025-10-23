package me.aloic.lazybot.command.osu;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.ScoreParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.CommandResultHandler;
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
    private CommandDatabaseProxy proxy;

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
        CommandResultHandler.uploadImageToOnebot(bot,event, playerService.scoreRank(scoreParameter));
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception {
        // only support in group
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Score Rank","Rank, Sr, Scorerank",
                        "查询本群绑定的玩家在一张地图上的成绩",
                        "LazyChildren", "Aloic", "2025-10-22")
                        .addExample("/Sr 4889657")
                        .addExample("/Rank 4889657+HDDT")
                        .addOption(new CommandParameter("Bid","查询的地图Id", CommandParameter.ParameterType.MUST))
                        .addOption(new CommandParameter("Mods","过滤的Mod", CommandParameter.ParameterType.OPTIONAL)));
    }

}
