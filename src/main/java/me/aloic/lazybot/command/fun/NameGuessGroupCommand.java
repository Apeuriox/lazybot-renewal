package me.aloic.lazybot.command.fun;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.GameWithTime;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@LazybotCommandMapping({"name","n"})
@Component
public class NameGuessGroupCommand extends NameGuessCommand implements LazybotSlashCommand
{
    @Resource
    private FunService funService;

    private final ConcurrentHashMap<String, GameWithTime> existingGameGroupMap = new ConcurrentHashMap<>();

    private static final Duration TIMEOUT = Duration.ofMinutes(3);


    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        Long groupId = event.getMessageEvent().getGroupId();
        GameWithTime original = existingGameGroupMap.get(groupId.toString());
        List<GroupMemberInfoResp> members = bot.getGroupMemberList(groupId).getData();
        if(CollectionUtils.isEmpty(members)) {
            return;
        }
        List<Long> userIds = members.stream().map(GroupMemberInfoResp::getUserId).toList();

        if(original == null){
            bot.sendGroupMsg(groupId, createNewGame(String.valueOf(groupId),userIds),false);
        }
        else {
            if (Duration.between(original.getStartTime(), LocalDateTime.now()).compareTo(TIMEOUT) > 0) {
                bot.sendGroupMsg(groupId, "[Lazybot] 时间到，正确答案为" + original.getOriginal(),false);
                existingGameGroupMap.remove(groupId.toString());
                bot.sendGroupMsg(groupId, createNewGame(String.valueOf(groupId), userIds),false);
            }
            else if(event.getScorePanelVersion()!=0){
                bot.sendGroupMsg(groupId, "[Lazybot] 已提前终止，正确答案为" + original.getOriginal(),false);
                existingGameGroupMap.remove(groupId.toString());
            }
            else {
                if (event.getCommandParameters()!=null && !event.getCommandParameters().isEmpty()) {
                    String username =String.join(" ", event.getCommandParameters());
                    bot.sendGroupMsg(groupId, checkUsernameGuess(existingGameGroupMap,username, original.getOriginal(), String.valueOf(groupId)),false);
                }
                else {
                    bot.sendGroupMsg(groupId, "[Lazybot] 请输入参数",false);
                }
            }
        }
    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
      // need group data
    }


    private String createNewGame(String identity, List<Long> userIds)
    {
        String targetUsername = funService.nameGuessGroupRandomName(userIds);
        if (targetUsername==null) throw new LazybotRuntimeException("获取数据为空");
        String obfuscatedName = obfuscateString(targetUsername);
        existingGameGroupMap.put(identity, new GameWithTime(targetUsername, LocalDateTime.now(), obfuscatedName));
        return "[Lazybot] 您的题目是 " + obfuscatedName;
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Player Name Guess","name, n",
                "从群聊中绑定Lazybot的用户中随机查询一位玩家的名字用于游戏，输入/name &以提前结束，一个群同时只能存在一场游戏",
                "Aloic", null, "2025-10-27")
                .addExample("/name")
                .addExample("/name &")
                .addOption(new CommandParameter("输入内容","开启游戏后答题的内容", CommandParameter.ParameterType.OPTIONAL)));
    }
}
