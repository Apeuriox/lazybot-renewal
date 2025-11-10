package me.aloic.lazybot.util;

import com.alibaba.fastjson2.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.enums.HTTPTypeEnum;
import me.aloic.lazybot.exception.LazybotNotFoundException;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotScorePerformance;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotWebPlayerPerformance;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotWebResult;
import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.BestPlay;
import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.HitScore;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScores;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.dto.sayobot.SayoData;
import me.aloic.lazybot.osu.dao.entity.dto.sayobot.SayobotBeatmapSet;
import me.aloic.lazybot.osu.dao.entity.dto.starmoon.ScoreStarMoon;
import me.aloic.lazybot.osu.dao.entity.dto.starmoon.StarMoonScoreWrapper;
import me.aloic.lazybot.osu.dao.entity.dto.starmoon.StarMoonUserWrapper;
import me.aloic.lazybot.osu.dao.entity.dto.starmoon.UserResponse;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.vo.HitScoreVO;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusPerformance;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.enums.OsuMod;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.monitor.TokenMonitor;
import me.aloic.lazybot.osu.utils.AssetDownloadUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DataExtractor
{
    @Resource
    private ApiRequestExecutor apiRequestExecutor;
    @Resource
    private TokenMapper tokenMapper;

    /**
     * 根据用户名和模式获取用户信息
     * @param playerName 用户名
     * @param mode 模式字符
     * @return 玩家信息DTO对象
     */

    public PlayerInfoDTO extractPlayerInfoDTO(String playerName, String mode)
    {
        try{
            PlayerInfoDTO playerInfoDTO = apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfPlayerInfo(playerName,mode),
                    HTTPTypeEnum.GET,
                    TokenMonitor.getToken(),
                    null,
                    PlayerInfoDTO.class);
            if(playerInfoDTO.getId()==null) {
                throw new LazybotRuntimeException("没这B人: " + playerName);
            }
            AccessTokenPO tokenPO = tokenMapper.selectByPlayername(playerName);
            return checkCachedAvatar(playerInfoDTO, tokenPO);
        }
        catch (LazybotNotFoundException e) {
            throw new LazybotRuntimeException("没这B人: " + playerName);
        }
    }

    public UserResponse extractPlayerStarMoon(String playerName)
    {
        try{
            String json = apiRequestExecutor.executeWithoutParse(
                    URLBuildUtil.buildURLOfStarMoonUserPage(playerName),
                    HTTPTypeEnum.GET,
                    null,
                    null);
            StarMoonUserWrapper user = apiRequestExecutor.parseResponse(TRPCParser.parseJSON(json), StarMoonUserWrapper.class, null);
            if (user == null) {
                throw new LazybotRuntimeException("没这B人: " + playerName);
            }
            return user.getResult().getData();
        }
        catch (LazybotRuntimeException lre) {
            throw lre;
        }
        catch (Exception e)
        {
            log.error("获取Star Moon用户时出错: " ,e);
            throw new LazybotRuntimeException("获取Star Moon用户时出错" + e.getMessage());
        }
    }
    public UserResponse extractPlayerStarMoon(Integer playerId)
    {
        try{
            String json = apiRequestExecutor.executeWithoutParse(
                    URLBuildUtil.buildURLOfStarMoonUserPage(playerId),
                    HTTPTypeEnum.GET,
                    null,
                    null);
            StarMoonUserWrapper user = apiRequestExecutor.parseResponse(TRPCParser.parseJSON(json), StarMoonUserWrapper.class, null);
            if (user == null) {
                throw new LazybotRuntimeException("没这B人: " + playerId);
            }
            return user.getResult().getData();
        }
        catch (LazybotRuntimeException lre) {
            throw lre;
        }
        catch (Exception e)
        {
            log.error("获取Star Moon用户时出错: " ,e);
            throw new LazybotRuntimeException("获取Star Moon用户时出错" + e.getMessage());
        }
    }

    public List<ScoreStarMoon> extractPlayerPerformanceStarMoon(String playerId, String mode, String subRuleset)
    {
        try{
            String json = apiRequestExecutor.executeWithoutParse(
                    URLBuildUtil.buildURLOfStarMoonBestPerformance(playerId,mode,subRuleset),
                    HTTPTypeEnum.GET,
                    null,
                    null);
            StarMoonScoreWrapper score = apiRequestExecutor.parseResponse(TRPCParser.parseJSON(json), StarMoonScoreWrapper.class, null);
            if (score == null || score.getResult().getData() == null)
            {
                throw new LazybotRuntimeException("无法找到指定成绩");
            }
            return score.getResult().getData();
        }
        catch (LazybotRuntimeException lre) {
            throw lre;
        }
        catch (Exception e)
        {
            log.error("处理Star Moon成绩时出错: " ,e);
            throw new LazybotRuntimeException("处理Star Moon成绩时出错" + e.getMessage());
        }
    }

    /**
     * 根据用户ID和模式获取用户信息
     * @param playerId 用户名
     * @param mode 模式字符
     * @return 玩家信息DTO对象
     */

    public PlayerInfoDTO extractPlayerInfoDTO(Integer playerId, String mode) {
       try{
           PlayerInfoDTO playerInfoDTO = apiRequestExecutor.execute(
                   URLBuildUtil.buildURLOfPlayerInfo(playerId,mode),
                   HTTPTypeEnum.GET,
                   TokenMonitor.getToken(),
                   null,
                   PlayerInfoDTO.class);
           if(playerInfoDTO.getId()==null) {
               throw new LazybotRuntimeException("没这B人: " + playerId);
           }
           AccessTokenPO tokenPO = tokenMapper.selectByPlayerId(playerId);
           return checkCachedAvatar(playerInfoDTO, tokenPO);
       }
       catch (LazybotNotFoundException e) {
           throw new LazybotRuntimeException("没这B人: " + playerId);
       }
    }

    public PlayerInfoDTO checkCachedAvatar(PlayerInfoDTO playerInfoDTO, AccessTokenPO tokenPO)
    {
        if (tokenPO == null)
            return playerInfoDTO;
        if (tokenPO.getAvatar_url()==null || !playerInfoDTO.getAvatar_url().equals(tokenPO.getAvatar_url())) {
            AssetDownloadUtil.avatarAbsolutePath(playerInfoDTO,true);
            tokenMapper.updateAvatar(playerInfoDTO.getAvatar_url(), playerInfoDTO.getId());
        }
        return playerInfoDTO;
    }


    /**
     * 根据用户ID获取PP+信息
     * @param playerId 用户ID
     * @return PP+玩家信息
     */
    public PPPlusPerformance extractPerformancePlusPlayerTotal(Integer playerId)
    {
        try{
            LazybotWebPlayerPerformance performance = apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfPlayerPerformancePlus(playerId),
                    HTTPTypeEnum.GET,
                    TokenMonitor.getLazybotToken(),
                    null,
                    LazybotWebPlayerPerformance.class);
            if(performance.getData()==null) {
                throw new LazybotRuntimeException("获取" + playerId + "用户pp+失败");
            }
            return performance.getData().getPerformances();
        }
        catch (LazybotNotFoundException e) {
            throw new LazybotRuntimeException("获取" + playerId + "用户pp+失败");
        }
    }

    /**
     * 根据用户ID更新用户PP+数据
     * @param playerId 用户ID
     * @return 更新后PP+玩家信息
     */
    public PPPlusPerformance extractPerformancePlusPlayerUpdate(Integer playerId)
    {
        try{
            LazybotWebPlayerPerformance performance = apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfUpdatePerformancePlus(playerId),
                    HTTPTypeEnum.POST,
                    TokenMonitor.getLazybotToken(),
                    null,
                    LazybotWebPlayerPerformance.class);
            if(performance.getData()==null) {
                throw new LazybotRuntimeException("更新" + playerId + "用户pp+失败");
            }
            return performance.getData().getPerformances();
        }
        catch (LazybotNotFoundException e) {
            throw new LazybotRuntimeException("更新" + playerId + "用户pp+失败");
        }
    }

    /**
     * 根据用户ID和地图ID添加成绩到PP+服务器
     * @param playerId 用户ID
     * @param beatmapId 地图ID
     * @return 该地图ID的PP+详情
     */
    public LazybotScorePerformance extractPerformancePlusAddScore(Integer playerId, Integer beatmapId)
    {
        try{
            LazybotWebResult<LazybotScorePerformance> result = apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfAddScorePerformancePlus(playerId,beatmapId),
                    HTTPTypeEnum.POST,
                    TokenMonitor.getLazybotToken(),
                    null,
                    new TypeReference<LazybotWebResult<LazybotScorePerformance>>() {});
            if(result.getData()==null) {
                throw new LazybotRuntimeException("添加用户" + playerId + "在" +beatmapId +"上的成绩失败");
            }
            return result.getData();
        }
        catch (LazybotNotFoundException e) {
            throw new LazybotRuntimeException("添加用户" + playerId + "在" +beatmapId +"上的成绩失败");
        }
    }

    /**
     * 获取用户的最近游玩成绩列表
     * @param playerId 用户ID
     * @param type 请求类型, 0会包含失败成绩
     * @param limit 请求最大返回数量
     * @param mode osu模式
     * @return Lazer成绩列表
     */
    public List<ScoreLazerDTO> extractRecentScoreList(Integer playerId, Integer type, Integer limit ,String mode)
    {
        try{
            List<ScoreLazerDTO> scoreLazerDTOS =  apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfRecentCommand(playerId,type,limit,mode),
                    HTTPTypeEnum.GET,
                    TokenMonitor.getToken(),
                    null,
                    new TypeReference<List<ScoreLazerDTO>>() {});
            if(scoreLazerDTOS==null|| scoreLazerDTOS.isEmpty()) throw new LazybotRuntimeException("小妹妹打都没打在这查哪个成绩呢");
            return scoreLazerDTOS;
        }
        catch (LazybotNotFoundException e) {
            throw new LazybotRuntimeException("小妹妹打都没打在这查哪个成绩呢");
        }
    }


    public BeatmapUserScoreLazer extractBeatmapUserScore(String beatmapId, Integer playerId, String mode, String modCombination)
    {
        try{
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
                throw new LazybotRuntimeException("没找到" + playerId + "在" + beatmapId +"上的成绩，" + " 模式为" + mode);
            return beatmapUserScoreLazer;
        }
        catch (LazybotNotFoundException e) {
            throw new LazybotRuntimeException("没找到" + playerId + "在" + beatmapId +"上的成绩，" + " 模式为" + mode);
        }
    }

    public List<ScoreLazerDTO> extractBeatmapUserScoreAll(Integer beatmapId, Integer playerId, String mode)
    {
        try{
            return apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfBeatmapScoreAll(String.valueOf(beatmapId), String.valueOf(playerId),mode),
                    HTTPTypeEnum.GET,
                    TokenMonitor.getToken(),
                    null,
                    BeatmapUserScores.class).getScores();
        }
        catch (LazybotNotFoundException e) {
            throw new LazybotRuntimeException("没有找到" + playerId +"在" + beatmapId+ "上的成绩");
        }

    }

    public BeatmapDTO extractBeatmap(String beatmapId, String mode)
    {
        try{
            BeatmapDTO beatmapDTO = apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfBeatmap(String.valueOf(beatmapId),mode),
                    HTTPTypeEnum.GET,
                    TokenMonitor.getToken(),
                    null,
                    BeatmapDTO.class);
            if(beatmapDTO.getId()==null) {
                throw new LazybotRuntimeException("找不到" + beatmapId + "在" +mode + "模式的地图");
            }
            return beatmapDTO;
        }
        catch (LazybotNotFoundException e) {
            throw new LazybotRuntimeException("找不到" + beatmapId + "在" +mode + "模式的地图");
        }
    }
    public List<ScoreLazerDTO> extractUserBestScoreList(String playerId, Integer offset , String mode)
    {
        try{
            List<ScoreLazerDTO> scoreLazerDTOS =  apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfUserBest(String.valueOf(playerId), offset, mode),
                    HTTPTypeEnum.GET,
                    TokenMonitor.getToken(),
                    null,
                    new TypeReference<List<ScoreLazerDTO>>() {});
            if(scoreLazerDTOS==null|| scoreLazerDTOS.isEmpty()) {
                throw new LazybotRuntimeException("没这成绩: " +"Index=" + offset+1 + " PlayerID=" + playerId);
            }
            return scoreLazerDTOS;
        }
        catch (LazybotNotFoundException e) {
            throw new LazybotRuntimeException("没这成绩: " +"Index=" + offset+1 + " PlayerID=" + playerId);
        }
    }


    public List<ScoreLazerDTO> extractUserBestScoreList(String playerId, Integer limit , Integer offset, String mode)
    {
       try{
           List<ScoreLazerDTO> scoreLazerDTOS = apiRequestExecutor.execute(
                   URLBuildUtil.buildURLOfUserBest(String.valueOf(playerId), limit, offset, mode),
                   HTTPTypeEnum.GET,
                   TokenMonitor.getToken(),
                   null,
                   new TypeReference<List<ScoreLazerDTO>>() {});
           if(scoreLazerDTOS==null|| scoreLazerDTOS.isEmpty()) {
               throw new LazybotRuntimeException("没这成绩: " +"index=" + (offset+1) + " player=" + playerId + " mode=" +mode);
           }
           return scoreLazerDTOS;
       }
       catch (LazybotNotFoundException e) {
           throw new LazybotRuntimeException("没这成绩: " +"index=" + (offset+1) + " player=" + playerId + " mode=" +mode);
       }
    }
    public List<ScoreLazerDTO> extractUserBestAll(String playerId, String mode)
    {
        try{
            List<ScoreLazerDTO> scoreLazerDTOS = apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfUserBest(String.valueOf(playerId), 100, 0, mode),
                    HTTPTypeEnum.GET,
                    TokenMonitor.getToken(),
                    null,
                    new TypeReference<List<ScoreLazerDTO>>() {}
            );
            if (scoreLazerDTOS == null || scoreLazerDTOS.isEmpty()) {
                throw new LazybotNotFoundException("找不到"+playerId+"的成绩");
            }
            if (scoreLazerDTOS.size() < 110) {
                scoreLazerDTOS.addAll(apiRequestExecutor.execute(
                        URLBuildUtil.buildURLOfUserBest(String.valueOf(playerId), 200-scoreLazerDTOS.size(), scoreLazerDTOS.size(), mode),
                        HTTPTypeEnum.GET,
                        TokenMonitor.getToken(),
                        null,
                        new TypeReference<List<ScoreLazerDTO>>() {}));
            }
            return scoreLazerDTOS;
        }
        catch (LazybotNotFoundException e) {
            throw new LazybotRuntimeException("找不到"+playerId+"的成绩");
        }
    }




    public List<HitScoreVO> extractOsuTrackHitScoreList(Integer playerId, String mode)
    {
        try{
            List<HitScoreVO> hitScoreVOs= TransformerUtil.HitScoreTransform(apiRequestExecutor.execute(
                    URLBuildUtil.buildURLOfOsuTrackScore(playerId, OsuMode.getMode(mode).getValue()),
                    HTTPTypeEnum.GET,
                    null,
                    null,
                    new TypeReference<List<HitScore>>() {}));
            if(hitScoreVOs.isEmpty()) {
                throw new LazybotRuntimeException("OsuTrack暂无数据");
            }
            return hitScoreVOs;
        }
        catch (LazybotNotFoundException e) {
            throw new LazybotRuntimeException("OsuTrack暂无数据");
        }
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
            throw new LazybotRuntimeException("OsuTrack BestPlay暂无数据");
        }
        return bestPlayList;
    }

    public SayoData extractSayobotBeatmapSet(Integer sid)
    {
        SayobotBeatmapSet sayobotBeatmapSet= apiRequestExecutor.execute(
                URLBuildUtil.buildURLOfSayobotBeatmap(sid),
                HTTPTypeEnum.GET,
                null,
                null,
                SayobotBeatmapSet.class);
        if(sayobotBeatmapSet==null || sayobotBeatmapSet.getData()==null) {
            throw new LazybotRuntimeException("Sayobot暂无数据");
        }
        return sayobotBeatmapSet.getData();
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

    /**
     * 根据用户ID获取玩家信息
     * @param userId 用户ID
     * @return 玩家信息DTO对象
     */
    public PlayerInfoDTO extractPlayerInfoByUserId(Long userId) {
        AccessTokenPO accessTokenPO = tokenMapper.selectByQq_code(userId);

        if(accessTokenPO == null) {
            return null;
        }
        return extractPlayerInfoDTO(accessTokenPO.getPlayer_id(), accessTokenPO.getDefault_mode());
    }

    /**
     * 批量获取用户信息(无序)
     * @param userIds 用户ID列表
     * @return 对应用户的玩家信息列表
     */
    public List<AccessTokenPO> extractPlayerInfoByUserIdBatch(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return tokenMapper.selectByCodes(userIds);
    }

}
