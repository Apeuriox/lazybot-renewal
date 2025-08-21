package me.aloic.lazybot.command.fun;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;
import me.aloic.lazybot.entity.SongGuessWithTime;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotSongGuessData;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.osu.utils.AssertDownloadUtil;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@LazybotCommandMapping({"song"})
@Component
public class SongGuessCommand implements LazybotSlashCommand
{
    @Resource
    private TestOutputTool testOutputTool;
    @Resource
    private FunService funService;
    @Resource
    private TokenMapper tokenMapper;
    private final ConcurrentHashMap<Long, SongGuessWithTime> existingGameMap = new ConcurrentHashMap<>();

    private static final Duration TIMEOUT = Duration.ofMinutes(5);


    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception
    {
        //not implemented yet
    }

    @Override
    public void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception
    {
        Long identity = event.getMessageEvent().getGroupId();
        AccessTokenPO tokenPO = tokenMapper.selectRandom();
        if (existingGameMap.get(identity) == null)
        {
            LazybotSongGuessData songGuessData = funService.songGuessImage(GeneralParameter.setupParameter(event,tokenPO));
            existingGameMap.put(identity, songGuessData.getMeta());
            ImageUploadUtil.uploadImageToOnebotWithText(bot,event,
                    songGuessData.getImg(),
                    "[Lazybot] 取自"+tokenPO.getPlayer_name()+"的BP前200\n缩放等级: " + songGuessData.getResizeLevel());
        }
        else
        {
            SongGuessWithTime original = existingGameMap.get(identity);
            if (Duration.between(original.getStartTime(), LocalDateTime.now()).compareTo(TIMEOUT) > 0) {
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(), "[Lazybot] 时间到，正确答案为: " + original.getTitle(),false);
                existingGameMap.remove(identity);
                LazybotSongGuessData songGuessData = funService.songGuessImage(GeneralParameter.setupParameter(event,tokenPO));
                existingGameMap.put(identity, songGuessData.getMeta());
                ImageUploadUtil.uploadImageToOnebotWithText(bot,event,
                        songGuessData.getImg(),
                        "[Lazybot] 取自"+tokenPO.getPlayer_name()+"的BP前200\n缩放等级: " + songGuessData.getResizeLevel());
            }
            else if (event.getScorePanelVersion()==0) {
                if (event.getCommandParameters() == null || event.getCommandParameters().isEmpty()) throw new LazybotRuntimeException("[Lazybot] 点击输入文本");
                String guessed = String.join(" ", event.getCommandParameters()).toLowerCase();
                JaroWinklerSimilarity jws = new JaroWinklerSimilarity();
                if (guessed.length()>original.getTitle().length()*1.5) {
                    throw new LazybotRuntimeException("[Lazybot] 我觉得你在瞎写");
                }
                double score = jws.apply(original.getTitle().toLowerCase(), guessed);
                if (score>0.7) {
                    existingGameMap.remove(identity);
                   try{
                       BufferedImage fullsize = ImageIO.read(new File(AssertDownloadUtil.svgAbsolutePath(original.getSid())));
                       ImageUploadUtil.uploadImageToOnebotWithText(bot,event,toByteArray(fullsize,"jpg"),
                               "[Lazybot] 回答正确，答案为: " + original.getTitle() +"\nBID: " + original.getBid());
                   }
                   catch (Exception e){
                       throw new LazybotRuntimeException("[Lazybot] 加载歌曲图片时出错");
                   }
                }
                else {
                    bot.sendGroupMsg(event.getMessageEvent().getGroupId(),"[Lazybot] 回答错误,输入/song &以获取提示，输出/song &&以提前结束",false);
                }
            }
            else if (event.getScorePanelVersion()==1){
                int rand=new Random().nextInt(2);
                if (rand==0) bot.sendGroupMsg(event.getMessageEvent().getGroupId(), "[Lazybot] 曲师为: " + original.getArtist(),false);
                else bot.sendGroupMsg(event.getMessageEvent().getGroupId(), "[Lazybot] 谱师为: " + original.getMapper(),false);
            }
            else {
                existingGameMap.remove(identity);
                try{
                    BufferedImage fullsize = ImageIO.read(new File(AssertDownloadUtil.svgAbsolutePath(original.getSid())));
                    ImageUploadUtil.uploadImageToOnebotWithText(bot,event,toByteArray(fullsize,"jpg"),
                            "[Lazybot] 已提前结束，答案为: " + original.getTitle() +"\nBID: " + original.getBid());
                }
                catch (Exception e){
                    throw new LazybotRuntimeException("[Lazybot] 加载歌曲图片时出错");
                }
            }
        }


    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        Long identity = 1524185356L;
        AccessTokenPO tokenPO = tokenMapper.selectRandom();
        if (existingGameMap.get(identity) == null)
        {
            LazybotSongGuessData songGuessData = funService.songGuessImage(GeneralParameter.setupParameter(event,tokenPO));
            existingGameMap.put(identity, songGuessData.getMeta());
            testOutputTool.saveImageAndTextToLocal(songGuessData.getImg(), "[Lazybot] 取自"+tokenPO.getPlayer_name()+"的bp200\n缩放等级:" + songGuessData.getResizeLevel());
        }
        else
        {
            SongGuessWithTime original = existingGameMap.get(identity);
            if (Duration.between(original.getStartTime(), LocalDateTime.now()).compareTo(TIMEOUT) > 0) {
                testOutputTool.writeStringToFile("[Lazybot] 时间到，正确答案为" + original.getTitle());
                existingGameMap.remove(identity);
                LazybotSongGuessData songGuessData = funService.songGuessImage(GeneralParameter.setupParameter(event,tokenPO));
                existingGameMap.put(identity, songGuessData.getMeta());
                testOutputTool.saveImageAndTextToLocal(songGuessData.getImg(), "[Lazybot] 取自"+tokenPO.getPlayer_name()+"的bp200");
            }
            else if (event.getScorePanelVersion()==0) {
                if (event.getCommandParameters() == null || event.getCommandParameters().isEmpty()) throw new LazybotRuntimeException("[Lazybot] 请输入文本");
                String guessed = String.join(" ", event.getCommandParameters());
                if (isFuzzyMatch(original.getTitle(),guessed,0.3)) {
                    existingGameMap.remove(identity);
                    try{
                        BufferedImage fullSize = ImageIO.read(new File(AssertDownloadUtil.svgAbsolutePath(original.getSid())));
                        testOutputTool.saveImageAndTextToLocal(toByteArray(fullSize,"jpg"),
                                "[Lazybot] 回答正确，答案为: " + original.getTitle() +"\nBID: " + original.getBid());
                    }
                    catch (Exception e){
                        throw new LazybotRuntimeException("[Lazybot] 加载歌曲图片时出错");
                    }
                }
                else {
                    testOutputTool.writeStringToFile("[Lazybot] 回答错误,输入/song &以获取提示，输出/song &&以提前结束");
                }
            }
            else if (event.getScorePanelVersion()==1){
                int rand=new Random().nextInt(2);
                if (rand==0)   testOutputTool.writeStringToFile("[Lazybot] 曲师为: " + original.getArtist());
                else  testOutputTool.writeStringToFile("[Lazybot] 谱师为: " + original.getMapper());
            }
            else {
                existingGameMap.remove(identity);
                try{
                    BufferedImage fullsize = ImageIO.read(new File(AssertDownloadUtil.svgAbsolutePath(original.getSid())));
                    testOutputTool.saveImageAndTextToLocal(toByteArray(fullsize,"jpg"),
                            "[Lazybot] 已提前结束，答案为: " + original.getTitle() +"\nBID: " + original.getBid());
                }
                catch (Exception e){
                    throw new LazybotRuntimeException("[Lazybot] 加载歌曲图片时出错");
                }
            }
        }


    }
    private String checkGuessed(String guessed, SongGuessWithTime original, Long identity)
    {
        if (original == null) throw new LazybotRuntimeException("[Lazybot] 还没有正在进行的游戏呢，请输入/song新建游戏");
        if (isFuzzyMatch(guessed,original.getTitle(),0.35))
        {
            existingGameMap.remove(identity);
            return "[Lazybot] 回答正确，答案为: " + original.getTitle() +"\nBID: " + original.getBid();
        }
        else {
            return "[Lazybot] 回答错误,输入/song &以获取提示，输出/song &&以提前结束";
        }
    }

    public static boolean isFuzzyMatch(String original, String input, double thresholdRatio) {
        if (original == null || input == null) return false;
        String cleanOriginal = original.toLowerCase().replaceAll("\\s+", "");
        String cleanInput = input.toLowerCase().replaceAll("\\s+", "");
        int matchLength = longestCommonSubsequence(cleanOriginal, cleanInput);
        int threshold = (int) Math.ceil(cleanOriginal.length() * thresholdRatio);
        return matchLength >= threshold;
    }

    public static int longestCommonSubsequence(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[a.length()][b.length()];
    }
    private static byte[] toByteArray(BufferedImage image, String format) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }
    @Override
    public String getHelp()
    {
        return HelpFormatter.format(new CommandHelp("Song Title Guess","song",
                "从绑定Lazybot的用户中随机查询一位玩家的的随机bp背景用于游戏，输入/song &以获取提示，输入/song &&以提前结束，一个群同时只能存在一场游戏，只保留最后的结果",
                "Aloic", null, "2025-07-30")
                .addExample("/song example")
                .addExample("/song &")
                .addOption(new CommandParameter("输入内容","开启游戏后答题的内容", CommandParameter.ParameterType.OPTIONAL)));
    }
}

