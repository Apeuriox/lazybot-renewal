package me.aloic.lazybot.util;

import com.alibaba.fastjson2.TypeReference;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import me.aloic.lazybot.enums.HTTPTypeEnum;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotWebPlayerPerformance;
import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.BestPlay;
import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.HitScore;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScores;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.vo.HitScoreVO;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusPerformance;
import me.aloic.lazybot.osu.enums.OsuMod;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.monitor.TokenMonitor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataExtractor
{
    @Resource
    private ApiRequestExecutor apiRequestExecutor;


    public PlayerInfoDTO extractPlayerInfoDTO(String playerName, String mode)
    {
        PlayerInfoDTO playerInfoDTO = apiRequestExecutor.execute(
                URLBuildUtil.buildURLOfPlayerInfo(playerName,mode),
                HTTPTypeEnum.GET,
                TokenMonitor.getToken(),
                null,
                PlayerInfoDTO.class);
        if(playerInfoDTO.getId()==null) {
            throw new LazybotRuntimeException("[Lazybot] 没这B人: " + playerName);
        }
        return playerInfoDTO;
    }

    public PlayerInfoDTO extractPlayerInfoDTO(Integer playerId, String mode) {
        PlayerInfoDTO playerInfoDTO = apiRequestExecutor.execute(
                URLBuildUtil.buildURLOfPlayerInfo(playerId,mode),
                HTTPTypeEnum.GET,
                TokenMonitor.getToken(),
                null,
                PlayerInfoDTO.class);
        if(playerInfoDTO.getId()==null) {
            throw new LazybotRuntimeException("[Lazybot] 没这B人: " + playerId);
        }
        return playerInfoDTO;
    }

    public PPPlusPerformance extractPerformancePlusPlayerTotal(Integer playerId)
    {
        LazybotWebPlayerPerformance performance = apiRequestExecutor.execute(
                URLBuildUtil.buildURLOfPlayerPerformancePlus(playerId),
                HTTPTypeEnum.GET,
                TokenMonitor.getLazybotToken(),
                null,
                LazybotWebPlayerPerformance.class);
        if(performance.getData()==null) {
            throw new LazybotRuntimeException("[Lazybot] 获取" + playerId + "用户pp+失败");
        }
        return performance.getData().getPerformances();
    }


    public List<ScoreLazerDTO> extractRecentScoreList(Integer playerId, Integer type, Integer limit ,String mode)
    {
        List<ScoreLazerDTO> scoreLazerDTOS =  apiRequestExecutor.execute(
                URLBuildUtil.buildURLOfRecentCommand(playerId,type,limit,mode),
                HTTPTypeEnum.GET,
                TokenMonitor.getToken(),
                null,
                new TypeReference<List<ScoreLazerDTO>>() {});

        if(scoreLazerDTOS==null|| scoreLazerDTOS.isEmpty()) {
            throw new LazybotRuntimeException("[Lazybot] 小妹妹打都没打在这查哪个成绩呢");
        }
        return scoreLazerDTOS;
    }


    public BeatmapUserScoreLazer extractBeatmapUserScore(String beatmapId, Integer playerId, String mode, String modCombination)
    {
        BeatmapUserScoreLazer beatmapUserScoreLazer;
        if (modCombination==null || modCombination.isEmpty()) {
            beatmapUserScoreLazer = apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfBeatmapScore(beatmapId, String.valueOf(playerId),mode),
                    HTTPTypeEnum.GET,
                    TokenMonitor.getToken(),
                    null,
                   BeatmapUserScoreLazer.class);
        }
        else {
            List<String> modsArray = OsuMod.getAllModAcronym(modCombination);
            beatmapUserScoreLazer = apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfBeatmapScore(beatmapId, String.valueOf(playerId),modsArray,mode),
                    HTTPTypeEnum.GET,
                    TokenMonitor.getToken(),
                    null,
                    BeatmapUserScoreLazer.class);
        }
        if(beatmapUserScoreLazer==null||beatmapUserScoreLazer.getScore()==null)
            throw new LazybotRuntimeException("[Lazybot] 没这成绩: Bid=" +beatmapId + " PlayerID=" + playerId +" Mode="+mode);
        return beatmapUserScoreLazer;
    }

    public List<ScoreLazerDTO> extractBeatmapUserScoreAll(Integer beatmapId, Integer playerId, String mode)
    {
        return apiRequestExecutor.execute(
                URLBuildUtil.buildURLOfBeatmapScoreAll(String.valueOf(beatmapId), String.valueOf(playerId),mode),
                HTTPTypeEnum.GET,
                TokenMonitor.getToken(),
                null,
                BeatmapUserScores.class).getScores();
    }

    public BeatmapDTO extractBeatmap(String beatmapId, String mode)
    {
        BeatmapDTO beatmapDTO = apiRequestExecutor.execute(
                URLBuildUtil.buildURLOfBeatmap(String.valueOf(beatmapId),mode),
                HTTPTypeEnum.GET,
                TokenMonitor.getToken(),
                null,
                BeatmapDTO.class);
        if(beatmapDTO.getId()==null) {
            throw new LazybotRuntimeException("[Lazybot] 没这地图: BID=" + beatmapId + " Mode=" +mode);
        }
        return beatmapDTO;
    }
    public List<ScoreLazerDTO> extractUserBestScoreList(String playerId, Integer offset , String mode)
    {
        List<ScoreLazerDTO> scoreLazerDTOS =  apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfUserBest(String.valueOf(playerId), offset, mode),
                    HTTPTypeEnum.GET,
                    TokenMonitor.getToken(),
                    null,
                    new TypeReference<List<ScoreLazerDTO>>() {});
        if(scoreLazerDTOS==null|| scoreLazerDTOS.isEmpty()) {
            throw new LazybotRuntimeException("[Lazybot] 没这成绩: " +"Index=" + offset+1 + " PlayerID=" + playerId);
        }
        return scoreLazerDTOS;
    }
    public List<ScoreLazerDTO> extractUserBestScoreList(String playerId, Integer limit , Integer offset, String mode)
    {
        List<ScoreLazerDTO> scoreLazerDTOS = apiRequestExecutor.execute(
                URLBuildUtil.buildURLOfUserBest(String.valueOf(playerId), limit, offset, mode),
                HTTPTypeEnum.GET,
                TokenMonitor.getToken(),
                null,
                new TypeReference<List<ScoreLazerDTO>>() {});
        if(scoreLazerDTOS==null|| scoreLazerDTOS.isEmpty()) {
            throw new LazybotRuntimeException("[Lazybot] 没这成绩: " +"index=" + (offset+1) + " player=" + playerId + " mode=" +mode);
        }
        return scoreLazerDTOS;
    }
    public List<HitScoreVO> extractOsuTrackHitScoreList(Integer playerId, String mode)
    {
        List<HitScoreVO> hitScoreVOs= TransformerUtil.HitScoreTransform(apiRequestExecutor.execute(
                URLBuildUtil.buildURLOfOsuTrackScore(playerId, OsuMode.getMode(mode).getValue()),
                HTTPTypeEnum.GET,
                null,
                null,
                new TypeReference<List<HitScore>>() {}));
        if(hitScoreVOs.isEmpty()) {
            throw new LazybotRuntimeException("[Lazybot] OsuTrack暂无数据");
        }
        return hitScoreVOs;
    }
    public List<BestPlay> extractOsuTrackBestPlay(Integer limit, Integer mode)
    {
        List<BestPlay> bestPlayList= apiRequestExecutor.execute(
                URLBuildUtil.buildURLOfOsuTrackBestPlays(limit,mode),
                HTTPTypeEnum.GET,
                null,
                null,
                new TypeReference<List<BestPlay>>() {});
        if(bestPlayList.isEmpty()) {
            throw new LazybotRuntimeException("[Lazybot] OsuTrack BestPlay暂无数据");
        }
        return bestPlayList;
    }
    public Integer extractRankByPP(String mode, Double pp)
    {
        String rankStr = apiRequestExecutor.execute(
                URLBuildUtil.buildURLOfPpRank(OsuMode.getMode(mode).getValue(), (int) Math.round(pp)),
                HTTPTypeEnum.GET,
                null,
                null);
        return Integer.parseInt(rankStr);
    }

}
