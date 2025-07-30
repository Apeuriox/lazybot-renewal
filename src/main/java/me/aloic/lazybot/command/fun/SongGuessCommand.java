package me.aloic.lazybot.command.fun;

import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.annotation.LazybotCommandMapping;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.component.CommandDatabaseProxy;
import me.aloic.lazybot.component.TestOutputTool;
import me.aloic.lazybot.entity.GameWithTime;
import me.aloic.lazybot.entity.SongGuessWithTime;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotSongGuessData;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.service.FunService;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.ImageUploadUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

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
    private CommandDatabaseProxy proxy;
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
        Long identity = event.getMessageEvent().getSender().getUserId();
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        if (existingGameMap.get(identity) == null)
        {
            LazybotSongGuessData songGuessData = funService.songGuessImage(GeneralParameter.setupParameter(event,tokenPO));
            existingGameMap.put(identity, songGuessData.getMeta());
            ImageUploadUtil.uploadImageToOnebotWithText(bot,event,
                    songGuessData.getImg(),
                    "取自"+tokenPO.getPlayer_name()+"的bp200");
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
                        "取自"+tokenPO.getPlayer_name()+"的bp200");
            }
            else if (event.getScorePanelVersion()==0) {
                if (event.getCommandParameters() == null) throw new LazybotRuntimeException("[Lazybot] 请输入文本");
                String guessed = String.join(" ", event.getCommandParameters());
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(), checkGuessed(guessed,original,identity),false);
            }
            else if (event.getScorePanelVersion()==1){
                int rand=new Random().nextInt(2);
                if (rand==0) bot.sendGroupMsg(event.getMessageEvent().getGroupId(), "[Lazybot] 曲师为: " + original.getArtist(),false);
                else bot.sendGroupMsg(event.getMessageEvent().getGroupId(), "[Lazybot] 谱师为: " + original.getMapper(),false);
            }
            else {
                existingGameMap.remove(identity);
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(), "[Lazybot] 正确答案为: " + original.getTitle() +"\n曲师: " + original.getArtist() +"\nBID: " + original.getBid(),false);
            }
        }


    }

    @Override
    public void execute(LazybotSlashCommandEvent event) throws Exception
    {
        Long identity = 1524185356L;
        AccessTokenPO tokenPO=proxy.getAccessToken(event);
        if (existingGameMap.get(identity) == null)
        {
            LazybotSongGuessData songGuessData = funService.songGuessImage(GeneralParameter.setupParameter(event,tokenPO));
            existingGameMap.put(identity, songGuessData.getMeta());
            testOutputTool.saveImageAndTextToLocal(songGuessData.getImg(), "[Lazybot] 取自"+tokenPO.getPlayer_name()+"的bp200");
        }
        else
        {
            SongGuessWithTime original = existingGameMap.get(identity);
            if (Duration.between(original.getStartTime(), LocalDateTime.now()).compareTo(TIMEOUT) > 0) {
                testOutputTool.writeStringToFile("[Lazybot] 已提前终止，正确答案为" + original.getTitle());
                existingGameMap.remove(identity);
                LazybotSongGuessData songGuessData = funService.songGuessImage(GeneralParameter.setupParameter(event,tokenPO));
                existingGameMap.put(identity, songGuessData.getMeta());
                testOutputTool.saveImageAndTextToLocal(songGuessData.getImg(), "[Lazybot] 取自"+tokenPO.getPlayer_name()+"的bp200");
            }
            else if (event.getScorePanelVersion()==0) {
                if (event.getCommandParameters() == null) throw new LazybotRuntimeException("[Lazybot] 请输入文本");
                String guessed = String.join(" ", event.getCommandParameters());
                testOutputTool.writeStringToFile(checkGuessed(guessed,original,identity));
            }
            else if (event.getScorePanelVersion()==1){
                int rand=new Random().nextInt(2);
                if (rand==0)   testOutputTool.writeStringToFile("[Lazybot] 曲师为: " + original.getArtist());
                else  testOutputTool.writeStringToFile("[Lazybot] 谱师为: " + original.getMapper());
            }
            else {
                existingGameMap.remove(identity);
                testOutputTool.writeStringToFile("[Lazybot] 正确答案为: " + original.getTitle() +"\nBID: " + original.getBid() + original.getArtist());
            }
        }


    }
    private String checkGuessed(String guessed, SongGuessWithTime original, Long identity)
    {
        if (original == null) throw new LazybotRuntimeException("[Lazybot] 还没有正在进行的游戏呢，请输入/song新建游戏");
        if (guessed.toLowerCase().trim().equals(original.getTitle().toLowerCase().trim()))
        {
            existingGameMap.remove(identity);
            return "[Lazybot] 正确答案为: " + original.getTitle() +"\nBID: " + original.getBid();
        }
        else {
            return "[Lazybot] 回答错误,输入/song &以获取提示，输出/song &&以提前结束";
        }
    }


}
