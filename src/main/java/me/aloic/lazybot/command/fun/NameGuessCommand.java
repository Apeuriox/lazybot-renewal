package me.aloic.lazybot.command.fun;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.GameWithTime;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@LazybotCommandMapping({"namelegacy","nl"})
@Component
public class NameGuessCommand implements LazybotSlashCommand
{
    @Resource
    private TestOutputTool testOutputTool;

    @Resource
    private TokenMapper tokenMapper;

    private final ConcurrentHashMap<String, GameWithTime> existingGameMap = new ConcurrentHashMap<>();

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
        GameWithTime original = existingGameMap.get(groupId.toString());

        if(original == null){
            bot.sendGroupMsg(groupId, createNewGame(String.valueOf(groupId)),false);
        }
        else {
            if (Duration.between(original.getStartTime(), LocalDateTime.now()).compareTo(TIMEOUT) > 0) {
                bot.sendGroupMsg(groupId, "[Lazybot] 时间到，正确答案为" + original.getOriginal(),false);
                existingGameMap.remove(groupId.toString());
                bot.sendGroupMsg(groupId, createNewGame(String.valueOf(groupId)),false);
            }
            else if(event.getScorePanelVersion()!=0){
                bot.sendGroupMsg(groupId, "[Lazybot] 已提前终止，正确答案为" + original.getOriginal(),false);
                existingGameMap.remove(groupId.toString());
            }
            else {
                if (event.getCommandParameters()!=null && !event.getCommandParameters().isEmpty()) {
                    String username =String.join(" ", event.getCommandParameters());
                    bot.sendGroupMsg(groupId, checkUsernameGuess(existingGameMap, username, original.getOriginal(), String.valueOf(groupId)),false);
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
        String identity = "TEST";
        GameWithTime original = existingGameMap.get(identity);

        if(original == null){
            testOutputTool.writeStringToFile(createNewGame(identity));
        }
        else {
            if (Duration.between(original.getStartTime(), LocalDateTime.now()).compareTo(TIMEOUT) > 0) {
                testOutputTool.writeStringToFile("[Lazybot] 时间到，正确答案为" + original.getOriginal());
                existingGameMap.remove(identity);
                testOutputTool.writeStringToFile(createNewGame(identity));
            }
            else if(event.getScorePanelVersion()!=0){
                testOutputTool.writeStringToFile("[Lazybot] 已提前终止，正确答案为" + original.getOriginal());
                existingGameMap.remove(identity);
            }
            else {
                if (event.getCommandParameters()!=null && !event.getCommandParameters().isEmpty()) {
                    String username = String.join(" ", event.getCommandParameters());
                    testOutputTool.writeStringToFile(checkUsernameGuess(existingGameMap,username, original.getOriginal(), identity));
                }
                else {
                    testOutputTool.writeStringToFile("[Lazybot] 请输入参数");
                }
            }
        }
    }

    protected String checkUsernameGuess(ConcurrentHashMap<String, GameWithTime> existingGameMap,String username, String original, String identity)
    {
        if (username == null) throw new LazybotRuntimeException("参数输入为空");
        if (original == null) throw new LazybotRuntimeException("还没有正在进行的游戏呢，请输入/name新建游戏");
        if (username.toLowerCase().trim().equals(original.toLowerCase().trim()))
        {
            existingGameMap.remove(identity);
            return "[Lazybot] 恭喜你，正确答案为" + original;
        }
        else
        {
            existingGameMap.get(identity).setMasked(revealOneChar(original,existingGameMap.get(identity).getMasked()));
            if (existingGameMap.get(identity).getMasked().equals(original))
            {
                existingGameMap.remove(identity);
                return "[Lazybot] 次数耗尽，正确答案为" + original;
            }
            return "[Lazybot] 回答错误，你的题目为 " + existingGameMap.get(identity).getMasked();
        }
    }

//    private static String obfuscateString(String input) {
//        int length = input.length();
//        if (length == 0) return "";
//
//        int keepCount = length < 4 ? 1 : new Random().nextInt(2) + 1;
//        keepCount = length < 8 ? keepCount : new Random().nextInt(3) + 3;
//        keepCount = length < 14 ? keepCount : new Random().nextInt(3) + 4;
//
//        List<Integer> digitIndices = new ArrayList<>();
//        for (int i = 0; i < length; i++) {
//            if (Character.isDigit(input.charAt(i))) {
//                digitIndices.add(i);
//            }
//        }
//        Set<Integer> keepIndices = new HashSet<>();
//
//        for (int i = 0; i < Math.min(keepCount, digitIndices.size()); i++) {
//            keepIndices.add(digitIndices.get(i));
//        }
//        Random rand = new Random();
//        while (keepIndices.size() < keepCount) {
//            int idx = rand.nextInt(length);
//            keepIndices.add(idx);
//        }
//
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < length; i++) {
//            if (keepIndices.contains(i)) {
//                result.append(input.charAt(i));
//            } else {
//                result.append('*');
//            }
//        }
//        return result.toString();
//    }
  protected static String obfuscateString(String input) {
        int length = input.length();
        if (length == 0) return "";

        // 计算需要保留的字符数量
        int keepCount;
        if (length <= 3) {
            keepCount = 1;
        } else if (length <= 5) {
            keepCount = 2;
        }
        else if (length <= 9) {
            keepCount = new Random().nextInt(2) + 2;
        } else {
            keepCount = new Random().nextInt(3) + 3;
        }

        // 收集可混淆的非空格字符的索引
        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (input.charAt(i) != ' ') {
                candidates.add(i);
            }
        }

        // 随机选择要保留的字符索引
        Collections.shuffle(candidates);
        Set<Integer> keepIndices = new HashSet<>();
        for (int i = 0; i < Math.min(keepCount, candidates.size()); i++) {
            keepIndices.add(candidates.get(i));
        }

        // 构建混淆后的字符串
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c == ' ') {
                result.append(' ');
            } else if (keepIndices.contains(i)) {
                result.append(c);
            } else {
                result.append('*');
            }
        }

        return result.toString();
    }

    protected static String revealOneChar(String original, String masked) {
        if (original == null || masked == null || original.length() != masked.length()) {
            throw new IllegalArgumentException("内部错误,原始字符串和混淆字符串长度不一致或为空");
        }

        List<Integer> unrevealedIndices = new ArrayList<>();
        for (int i = 0; i < masked.length(); i++) {
            if (masked.charAt(i) == '*') {
                unrevealedIndices.add(i);
            }
        }

        if (unrevealedIndices.isEmpty()) {
            return masked;
        }

        int revealIndex = unrevealedIndices.get(new Random().nextInt(unrevealedIndices.size()));
        StringBuilder result = new StringBuilder(masked);
        result.setCharAt(revealIndex, original.charAt(revealIndex));
        return result.toString();
    }
    protected String createNewGame(String identity)
    {
        AccessTokenPO token = tokenMapper.selectRandom();
        if (token==null) throw new LazybotRuntimeException("数据库数据不足或获取失败");
        String obfuscatedName = obfuscateString(token.getPlayer_name());
        existingGameMap.put(identity, new GameWithTime(token.getPlayer_name(), LocalDateTime.now(),obfuscatedName));
        return "[Lazybot] 您的题目是 " + obfuscatedName;
    }

    @Override
    public String getHelp()
    {
        return HelpFormatter.format(
                new CommandHelp("Player Name Guess旧版","nl",
                "从绑定Lazybot的用户中随机查询一位玩家的名字用于游戏，仅限初次绑定时缓存，输入/name &以提前结束，一个群同时只能存在一场游戏",
                "Aloic", null, "2025-07-30")
                .addExample("/nl")
                .addExample("/nl &")
                .addOption(new CommandParameter("输入内容","开启游戏后答题的内容", CommandParameter.ParameterType.OPTIONAL)));
    }
}
