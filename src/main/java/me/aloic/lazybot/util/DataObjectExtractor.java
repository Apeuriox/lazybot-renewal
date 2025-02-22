package me.aloic.lazybot.util;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.BestPlay;
import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.HitScore;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScores;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.entity.vo.HitScoreVO;
import me.aloic.lazybot.osu.enums.OsuMod;
import me.aloic.lazybot.osu.enums.OsuMode;

import java.util.List;

public class DataObjectExtractor
{
    public static PlayerInfoDTO extractPlayerInfo(String accessToken, String playerName, String mode)
    {
        ApiRequestStarter requestStarter = new ApiRequestStarter(URLBuildUtil.buildURLOfPlayerInfo(playerName,mode),accessToken);
        PlayerInfoDTO playerInfoDTO = requestStarter.executeRequest(ContentUtil.HTTP_REQUEST_TYPE_GET, PlayerInfoDTO.class);
        if(playerInfoDTO.getId()==null) {
            throw new LazybotRuntimeException("没这B人: " + playerName);
        }
        return playerInfoDTO;
    }
    public static PlayerInfoDTO extractPlayerInfo(String accessToken, Integer playerId, String mode)
    {
        ApiRequestStarter requestStarter = new ApiRequestStarter(URLBuildUtil.buildURLOfPlayerInfo(playerId,mode),accessToken);
        PlayerInfoDTO playerInfoDTO = requestStarter.executeRequest(ContentUtil.HTTP_REQUEST_TYPE_GET, PlayerInfoDTO.class);
        if(playerInfoDTO.getId()==null) {
            throw new LazybotRuntimeException("没这B人: " + playerId);
        }
        return playerInfoDTO;
    }


    public static List<ScoreLazerDTO> extractRecentScoreList(String accessToken, Integer playerId, Integer type, String mode)
    {
        List<ScoreLazerDTO> scoreLazerDTOS =  new ApiRequestStarter(URLBuildUtil.buildURLOfRecentCommand(playerId,type,mode),accessToken)
                .executeRequestForList(ContentUtil.HTTP_REQUEST_TYPE_GET, ScoreLazerDTO.class);
        if(scoreLazerDTOS==null|| scoreLazerDTOS.isEmpty()) {
            throw new LazybotRuntimeException("小妹妹打都没打在这查哪个成绩呢");
        }
        return scoreLazerDTOS;
    }
    public static String checkUserLink(UserTokenPO tokenPO)
    {
        if (tokenPO == null)
            throw new LazybotRuntimeException("'未查到相关信息，请使用/link 你的用户名 进行绑定  ");
        return tokenPO.getPlayer_name();
    }


    public static BeatmapUserScoreLazer extractBeatmapUserScore(String accessToken, String beatmapId, Integer playerId, String mode, String modCombination)
    {
        ApiRequestStarter scoreApiRequestStarter;
        if (modCombination==null || modCombination.isEmpty()) {
            scoreApiRequestStarter = new ApiRequestStarter(URLBuildUtil.buildURLOfBeatmapScore(beatmapId, String.valueOf(playerId),mode),accessToken);
        }
        else {
            List<String> modsArray = OsuMod.getAllModAcronym(modCombination);
            scoreApiRequestStarter = new ApiRequestStarter(URLBuildUtil.buildURLOfBeatmapScore(beatmapId, String.valueOf(playerId),modsArray,mode),accessToken);
        }
        BeatmapUserScoreLazer beatmapUserScoreLazer = scoreApiRequestStarter.executeRequest(ContentUtil.HTTP_REQUEST_TYPE_GET, BeatmapUserScoreLazer.class);
        if(beatmapUserScoreLazer==null||beatmapUserScoreLazer.getScore()==null)
            throw new LazybotRuntimeException("没这成绩: BID=" +beatmapId + " player=" + playerId +" mode="+mode);
        return beatmapUserScoreLazer;
    }

    public static List<ScoreLazerDTO> extractBeatmapUserScoreAll(String accessToken, Integer beatmapId, Integer playerId, String mode)
    {
        return new ApiRequestStarter(URLBuildUtil.buildURLOfBeatmapScoreAll(String.valueOf(beatmapId), String.valueOf(playerId),mode),accessToken)
                .executeRequest(ContentUtil.HTTP_REQUEST_TYPE_GET, BeatmapUserScores.class).getScores();
    }

    public static BeatmapDTO extractBeatmap(String accessToken, String beatmapId, String mode)
    {
        BeatmapDTO beatmapDTO = new ApiRequestStarter(URLBuildUtil.buildURLOfBeatmap(beatmapId,mode),accessToken)
                .executeRequest(ContentUtil.HTTP_REQUEST_TYPE_GET, BeatmapDTO.class);
        if(beatmapDTO.getId()==null) {
            throw new LazybotRuntimeException("没这地图: BID=" + beatmapId + " mode=" +mode);
        }
        return beatmapDTO;
    }
    public static List<ScoreLazerDTO> extractUserBestScoreList(String accessToken, String playerId, Integer offset , String mode)
    {
        List<ScoreLazerDTO> scoreLazerDTOS =  new ApiRequestStarter(URLBuildUtil.buildURLOfUserBest(playerId,offset,mode),accessToken)
                .executeRequestForList(ContentUtil.HTTP_REQUEST_TYPE_GET, ScoreLazerDTO.class);
        if(scoreLazerDTOS==null|| scoreLazerDTOS.isEmpty()) {
            throw new LazybotRuntimeException("没这成绩: " +"index=" + offset+1 + " player=" + playerId);
        }
        return scoreLazerDTOS;
    }
    public static List<ScoreLazerDTO> extractUserBestScoreList(String accessToken, String playerId, Integer limit , Integer offset, String mode)
    {
        List<ScoreLazerDTO> scoreLazerDTOS =  new ApiRequestStarter(URLBuildUtil.buildURLOfUserBest(playerId,limit,offset,mode),accessToken)
                .executeRequestForList(ContentUtil.HTTP_REQUEST_TYPE_GET, ScoreLazerDTO.class);
        if(scoreLazerDTOS==null|| scoreLazerDTOS.isEmpty()) {
            throw new LazybotRuntimeException("没这成绩: " +"index=" + (offset+1) + " player=" + playerId + " mode=" +mode);
        }
        return scoreLazerDTOS;
    }
    public static List<HitScoreVO> extractOsuTrackHitScoreList(Integer playerId, String mode)
    {
        ApiRequestStarter apiRequestStarter = new ApiRequestStarter(URLBuildUtil.buildURLOfOsuTrackScore(playerId, OsuMode.getMode(mode).getValue()));
        java.util.List<HitScoreVO> hitScoreVOs= TransformerUtil.HitScoreTransform(apiRequestStarter.executeRequestForList(ContentUtil.HTTP_REQUEST_TYPE_GET, HitScore.class));
        if(hitScoreVOs.isEmpty()) {
            throw new LazybotRuntimeException("暂无数据");
        }
        return hitScoreVOs;
    }
    public static List<BestPlay> extractOsuTrackBestPlay(Integer limit, Integer mode)
    {
        ApiRequestStarter apiRequestStarter = new ApiRequestStarter(URLBuildUtil.buildURLOfOsuTrackBestPlays(limit,mode));
        List<BestPlay> bestPlayList= apiRequestStarter.executeRequestForList(ContentUtil.HTTP_REQUEST_TYPE_GET, BestPlay.class);
        if(bestPlayList.isEmpty()) {
            throw new LazybotRuntimeException("暂无数据");
        }
        return bestPlayList;
    }
}
