package me.aloic.lazybot.osu.service.ServiceImpl;

import com.alibaba.fastjson.JSON;
import desu.life.RosuFFI;
import jakarta.annotation.Resource;
import me.aloic.lazybot.entity.command.*;
import me.aloic.lazybot.entity.po.BadgeUserShowcasePO;
import me.aloic.lazybot.entity.vo.ThumbnailClassicalVO;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.CompareMonitor;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.plus.LazybotScorePerformance;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.dto.plus.ScorePerformanceDTO;
import me.aloic.lazybot.osu.dao.entity.dto.starmoon.ScoreStarMoon;
import me.aloic.lazybot.osu.dao.entity.dto.starmoon.UserResponse;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import me.aloic.lazybot.osu.dao.entity.vo.*;
import me.aloic.lazybot.osu.dao.mapper.*;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.ScorePerformanceDimension;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.theme.Color.HSL;
import me.aloic.lazybot.osu.theme.preset.ProfileLightTheme;
import me.aloic.lazybot.osu.theme.preset.ProfileTheme;
import me.aloic.lazybot.osu.utils.*;
import me.aloic.lazybot.parameter.*;
import me.aloic.lazybot.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.osu.extended.rosu.JniBeatmap;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService
{
    private static final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);
    @Resource
    private DataExtractor dataExtractor;
    @Resource
    private CustomizationMapper customizationMapper;
    @Resource
    private BadgeShowcaseMapper badgeMapper;
    @Resource
    private TokenMapper tokenMapper;
    @Resource
    private OsuToolsUtil osuToolsUtil;
    @Resource
    private AssetDownloader assetDownloader;


    @Override
    public ScoreVO getUserHighestScoreOnMap(ScoreParameter params)
    {
        int playerId = params.getPlayerId();
        boolean easterTrigger = CommonTool.shouldTriggerEaster();
        if (easterTrigger && !Objects.equals(params.getMode(), "osu")) easterTrigger=false;
        PlayerInfoDTO player = null;
        if (params.getPlayerName()!=null) {
            player=dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode());
            playerId = player.getId();
        }
        BeatmapUserScoreLazer beatmapUserScoreLazer = dataExtractor.extractBeatmapUserScore(
                String.valueOf(params.getBeatmapId()),
                playerId,
                params.getMode(),
                params.getModCombination()
        );
        ScoreVO scoreVO = osuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(params.getBeatmapId()), params.getMode()),
                beatmapUserScoreLazer.getScore(),
                false);
        verifyBeatmapsCache(scoreVO);
        if(easterTrigger) {
            if (player==null) player=dataExtractor.extractPlayerInfoDTO(params.getPlayerId(),params.getMode());
            scoreVO.setRawPlayerData(player);
            params.setVersion(727);
        }
        if (params.getChannelId()!=null && params.getChannelId()!=1919810L)
            CompareMonitor.saveRecentBeatmap(params.getChannelId(), scoreVO.getBeatmap().getBid());
       return scoreVO;
    }
    @Override
    public PPPlusScore getUserHighestScoreOnMapPlus(ScoreParameter params) throws Exception
    {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("QuadraGrid样式仅支持Std模式，因为其他模式没有PP+数据");
        ScoreVO scoreVO = getUserHighestScoreOnMap(params);
        return setupPlusScore(scoreVO);
    }



    @Override
    public UserAllScore getUserAllScoresOnMap(ScoreParameter params) throws Exception {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);

        List<ScoreLazerDTO> scoreList = dataExtractor.extractBeatmapUserScoreAll(params.getBeatmapId(), playerInfoDTO.getId(), params.getMode());
        if (scoreList==null || scoreList.isEmpty()) throw new LazybotRuntimeException("没有找到" + playerInfoDTO.getUsername() +"在" + params.getBeatmapId()+ "上的成绩");
        List<MapScore> mapScoreList=TransformerUtil.mapScoreTransform(scoreList);

        OsuToolsUtil.setupPlayerStatics(mapScoreList,playerInfoDTO);
        BeatmapDTO beatmapDTO = dataExtractor.extractBeatmap(String.valueOf(params.getBeatmapId()),params.getMode());
        BeatmapPerformance beatmapPerformance=TransformerUtil.beatmapPerformanceTransform(beatmapDTO);
        mapScoreList = setupMapScores(mapScoreList, beatmapPerformance, Comparator.comparing(MapScore::getPp), beatmapDTO.getChecksum());
        if (params.getChannelId()!=null && params.getChannelId()!=1919810L)
            CompareMonitor.saveRecentBeatmap(params.getChannelId(), params.getBeatmapId());
        return new UserAllScore(mapScoreList,beatmapPerformance);
    }
    @Override
    public BeatmapStatistics getBeatmapStatisticsWithImaginaryParams(BeatmapStatisticsParameter params) throws Exception {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("暂不支持其他模式，请等待更新");

        BeatmapDTO beatmapDTO = dataExtractor.extractBeatmap(String.valueOf(params.getBeatmapId()),params.getMode());
        BeatmapPerformance beatmapPerformance=TransformerUtil.beatmapPerformanceTransform(beatmapDTO);

        BeatmapStatistics result = new BeatmapStatistics(beatmapPerformance,params.getTargetAccuracy());
        PlayerInfoDTO mapper = dataExtractor.extractPlayerInfoDTO(beatmapPerformance.getCreator().trim(),params.getMode());
        result.setMapperAvatarUrl(OsuToolsUtil.getOsuAvatarUrl(mapper));
        result.setMapBackgroundUrl(osuToolsUtil.getBeatmapUrl(result.getBeatmap().getSid()));
        result.setMode(OsuMode.getMode(params.getMode()));
        result.setImaginaryMods(OsuToolsUtil.wireModEntities(List.of(params.getModCombination().split("(?<=\\G.{2})"))));
        RosuUtil.setupBeatmapStatistics(result);
        double weightAim = Math.pow(result.getPerformance().getAimPP(), 1.1);
        double weightSpeed = Math.pow(result.getPerformance().getSpdPP(), 1.1);
        double weightAccuracy = Math.pow(result.getPerformance().getAccPP(), 1.1);
        double totalWeight = weightAim + weightSpeed + weightAccuracy;
        int ratioAim = (int) Math.round(weightAim * 100.0 / totalWeight);
        int ratioSpeed = (int) Math.round(weightSpeed * 100.0 / totalWeight);
        result.setPpBreakdownRatioChain(ratioAim +"%-" + ratioSpeed+"%-" + (100-ratioAim-ratioSpeed) +"%");
        ModCalculatorUtil.afterModMapInfo(result.getBeatmap(), result.getImaginaryMods());
        return result;
    }



    @Override
    public ThumbnailClassicalVO thumbnailClassicalScore(ThumbnailParameter params)
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);
        List<ScoreLazerDTO> scoreList = dataExtractor.extractBeatmapUserScoreAll(params.getBeatmapId(), playerInfoDTO.getId(), params.getMode());
        if (scoreList==null || scoreList.isEmpty()) throw new LazybotRuntimeException("没有找到" + playerInfoDTO.getUsername() +"在" + params.getBeatmapId()+ "上的成绩");
        scoreList.get(params.getIndex()-1).setUser(playerInfoDTO);
        ScoreVO scoreVO = osuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(params.getBeatmapId()), params.getMode()),
                scoreList.get(params.getIndex()-1),
                false);
        if (!scoreVO.getIsLazer()) {
            scoreVO.setModJSON(scoreVO.getModJSON().stream().filter(mod -> !mod.getAcronym().equals("CL")).toList());
        }
        return setupThumbnailClassicalVO(params, playerInfoDTO, scoreVO);
    }


    @Override
    public ThumbnailClassicalVO thumbnailClassicalRecent(ThumbnailParameter params)
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);
        List<ScoreLazerDTO> scoreList = dataExtractor.extractRecentScoreList(playerInfoDTO.getId(), 1, params.getIndex(), params.getMode());
        if(params.getIndex()>scoreList.size()) {
            throw new LazybotRuntimeException("超出能索引的最大距离，当前为: "+params.getIndex()+", 最大为: " + scoreList.size());
        }
        scoreList.get(params.getIndex()-1).setUser(playerInfoDTO);
        ScoreVO scoreVO = osuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(scoreList.get(params.getIndex() - 1).getBeatmap_id()), params.getMode()),
                scoreList.get(params.getIndex()-1),
                false);
        if (!scoreVO.getIsLazer())
        {
            scoreVO.setModJSON(scoreVO.getModJSON().stream().filter(mod -> !mod.getAcronym().equals("CL")).toList());
        }
        verifyBeatmapsCache(scoreVO);
        return setupThumbnailClassicalVO(params, playerInfoDTO, scoreVO);
    }

    @NotNull
    public ThumbnailClassicalVO setupThumbnailClassicalVO(ThumbnailParameter params, PlayerInfoDTO playerInfoDTO, ScoreVO scoreVO)
    {
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        ThumbnailClassicalVO tbc = new ThumbnailClassicalVO(info,scoreVO,params.getComment(),
                params.getPosition()==null ? null:String.valueOf(params.getPosition())
        );
        if (params.getAttributes()!=null && !params.getAttributes().isEmpty())
        {
            tbc.setAttributes(params.getAttributes());
        }
        return tbc;
    }

    @Override
    public ScoreVO getUserRecentScoreList(RecentParameter params, int type)
    {
        boolean easterTrigger = CommonTool.shouldTriggerEaster();
        if (easterTrigger && !Objects.equals(params.getMode(), "osu")) easterTrigger=false;

        PlayerInfoDTO player = null;
        if (params.getPlayerName()!=null) {
            player=dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode());
            params.setPlayerId(player.getId());
        }
        List<ScoreLazerDTO> scoreList = dataExtractor.extractRecentScoreList(params.getPlayerId(), type, params.getIndex(), params.getMode());
        if(params.getIndex()>scoreList.size()) {
            throw new LazybotRuntimeException("当前超出能索引的最大距离，当前为: "+params.getIndex());
        }
        ScoreVO scoreVO = osuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(scoreList.get(params.getIndex() - 1).getBeatmap_id()), params.getMode()),
                scoreList.get(params.getIndex() - 1),
                false);
        verifyBeatmapsCache(scoreVO);
        if(easterTrigger) {
            if (player==null) player=dataExtractor.extractPlayerInfoDTO(params.getPlayerId(),params.getMode());
            scoreVO.setRawPlayerData(player);
            params.setVersion(727);
        }
        if (params.getChannelId()!=null)
            CompareMonitor.saveRecentBeatmap(params.getChannelId(), scoreVO.getBeatmap().getBid());
        return scoreVO;
    }



    @Override
    public PPPlusScore getUserRecentScoreListPlus(RecentParameter params, int type) throws RosuFFI.FFIException
    {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("QuadraGrid样式仅支持Std模式，因为其他模式没有PP+数据");
        ScoreVO scoreVO = getUserRecentScoreList(params, type);
        return setupPlusScore(scoreVO);
    }

    @Override
    public ScoreVO getUserBestPerformanceSingle(BpParameter params)
    {
        boolean easterTrigger = CommonTool.shouldTriggerEaster();
        if (easterTrigger && !Objects.equals(params.getMode(), "osu")) easterTrigger=false;

        PlayerInfoDTO player = null;
        if (params.getPlayerName()!=null) {
            player=dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode());
            params.setPlayerId(player.getId());
        }
        List<ScoreLazerDTO> scoreDTO = dataExtractor.extractUserBestScoreList(
                String.valueOf(params.getPlayerId()),
                params.getIndex()-1,
                params.getMode());

        ScoreVO scoreVO = osuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(scoreDTO.getFirst().getBeatmap_id()),params.getMode()),
                scoreDTO.getFirst(),
                false);
        verifyBeatmapsCache(scoreVO);
        CompareMonitor.saveRecentBeatmap(params.getChannelId(), scoreVO.getBeatmap().getBid());

        if(easterTrigger) {
            if (player==null) player=dataExtractor.extractPlayerInfoDTO(params.getPlayerId(),params.getMode());
            scoreVO.setRawPlayerData(player);
            params.setVersion(727);
        }

        return scoreVO;
    }

    @Override
    public ScoreVO getUserBestPerformanceSingleStarMoon(BpParameter params)
    {
        List<ScoreStarMoon> score = dataExtractor.extractPlayerPerformanceStarMoon(
                String.valueOf(params.getPlayerId()),
                params.getMode(),
                params.getSubRuleset().getDescribe());
        ScoreStarMoon targetScore;
        try{
            targetScore = score.get(params.getIndex()-1);
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("超出能索引的最大距离，当前为: "+params.getIndex()+", 最大为: " + score.size());
        }
        UserResponse user = dataExtractor.extractPlayerStarMoon(params.getPlayerId());

        ScoreVO scoreVO = osuToolsUtil.setupScoreVO(targetScore,
                user,
                params.getMode(),
                false);
        verifyBeatmapsCache(scoreVO);
        CompareMonitor.saveRecentBeatmap(params.getChannelId(), scoreVO.getBeatmap().getBid());
        return scoreVO;
    }

    @Override
    public PPPlusScore getUserBestPerformanceSinglePlus(BpParameter params) throws RosuFFI.FFIException
    {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("QuadraGrid样式仅支持Std模式，因为其他模式没有PP+数据");
        ScoreVO scoreVO = getUserBestPerformanceSingle(params);
        return setupPlusScore(scoreVO);
    }


    @Override
    public PlayerScoreList bplistCardView(BplistParameter params)
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        List<ScoreLazerDTO> scoreDTOS=dataExtractor.extractUserBestScoreList(
                String.valueOf(playerInfoDTO.getId()),
                params.getTo()-params.getFrom()+1,
                params.getFrom()-1,
                params.getMode());
        List<ScoreVO> scoreVOArray= osuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreDTOS));
        return new PlayerScoreList(scoreVOArray, info);
    }
    @Override
    public PlayerScoreList bplistCardViewStarMoon(BplistParameter params)
    {
        List<ScoreStarMoon> score = dataExtractor.extractPlayerPerformanceStarMoon(
                String.valueOf(params.getPlayerId()),
                params.getMode(),
                params.getSubRuleset().getDescribe());
        List<ScoreStarMoon> scoreList = score.stream().limit(params.getTo()).toList();
        UserResponse user = dataExtractor.extractPlayerStarMoon(params.getPlayerId());
        List<ScoreVO> scoreVOArray= osuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreList,user,params.getMode()));
        return new PlayerScoreList(scoreVOArray, TransformerUtil.userTransform(user, params.getSubRuleset(), params.getMode()));
    }


    @Override
    public PlayerScoreList bpScoreFilter(ScoreFilterParameter params) throws Exception
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        CompletableFuture<List<ScoreLazerDTO>> futurePage1 = CompletableFuture.supplyAsync(() ->
                dataExtractor.extractUserBestScoreList(String.valueOf(info.getId()), 100, 0, params.getMode())
        );
        CompletableFuture<List<ScoreLazerDTO>> futurePage2 = CompletableFuture.supplyAsync(() ->
                dataExtractor.extractUserBestScoreList(String.valueOf(info.getId()), 100, 101, params.getMode())
        );

        List<ScoreLazerDTO> scoreDTOList = futurePage1.get();
        if (scoreDTOList.size() < 110) {
            scoreDTOList.addAll(futurePage2.get());
        }

        for(int i=0;i<scoreDTOList.size();i++) {
            scoreDTOList.get(i).setPosition(i);
        }
        OsuToolsUtil.setupModStats(scoreDTOList);
        List<ScoreLazerDTO> filteredScores = scoreDTOList.stream()
                .filter(score -> params.getFilters().stream().allMatch(f -> f.filter(score)))
                .sorted(Comparator.comparing(ScoreLazerDTO::getPp).reversed())
                .limit(51)
                .toList();
        if(filteredScores.isEmpty()) throw new LazybotRuntimeException("没有找到符合条件的bp");

        List<ScoreVO> scoreVOList=TransformerUtil.scoreTransformForListWithIndex(filteredScores);
        osuToolsUtil.setUpImageStatic(scoreVOList);
        return new PlayerScoreList(scoreVOList,info);
    }

    @Override
    public PlayerScoreList playRecentSeries(SeriesParameter params, int type, int style)
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        int maxIndex = params.getMaxIndex()>50 ? 50 : params.getMaxIndex();
        List<ScoreLazerDTO> scoreDTOS= dataExtractor.extractRecentScoreList(
                info.getId(),
                type,
                maxIndex,
                params.getMode());
        if (style==0)
        {
            List<ScoreVO> scoreVOArray = osuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreDTOS));
            return new PlayerScoreList(scoreVOArray,info);
        }
        else {
            List<ScoreSequence> scoreSequences=TransformerUtil.scoreSequenceListTransform(scoreDTOS,true);
            osuToolsUtil.setUpImageStaticSequence(scoreSequences);
            return new PlayerScoreList(info, scoreSequences);
        }
    }

    @Override
    public PlayerScoreList getPlayerTodayNewBps(TodaybpParameter params)
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);

        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        List<ScoreLazerDTO> scoreDTOList=dataExtractor.extractUserBestScoreList(
                String.valueOf(info.getId()),
                100,0,params.getMode());
        if (scoreDTOList.size() < 110) {
            scoreDTOList.addAll(dataExtractor.extractUserBestScoreList(
                    String.valueOf(info.getId()),
                    100,101,params.getMode()));
        }
        //Why not directly filter scoreDTOs? cuz we need this procedure to set up Indexes
        List<ScoreVO> scoreVOList=TransformerUtil.scoreTransformForList(scoreDTOList);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC+0"));
        scoreVOList = scoreVOList.stream()
                .filter(score -> {
                    ZonedDateTime scoreTime = ZonedDateTime.parse(score.getCreate_at(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    return scoreTime.isAfter(now.minusDays(params.getMaxDays()));
                }).collect(Collectors.toList());
        if(scoreVOList.isEmpty()) throw new LazybotRuntimeException("没有找到符合条件的bp");

        osuToolsUtil.setUpImageStatic(scoreVOList);
        return new PlayerScoreList(scoreVOList,info);
    }


    @Override
    public ComparePlayerBpList bpvs(BpvsParameter params)throws Exception
    {
        CompletableFuture<PlayerInfoDTO> playerInfoFuture = CompletableFuture.supplyAsync(() -> {
            try {
                PlayerInfoDTO dto;
                if (params.getPlayerName()!=null) dto= dataExtractor.extractPlayerInfoDTO(params.getPlayerName(), params.getMode());
                else dto= dataExtractor.extractPlayerInfoDTO(params.getPlayerId(), params.getMode());
                dto.setAvatar_url(AssetDownloadUtil.avatarAbsolutePath(dto, false));
                return dto;
            } catch (Exception e) {
                throw new LazybotRuntimeException("异步获取玩家" + params.getPlayerName() + "数据失败"+ e.getMessage());
            }
        });

        CompletableFuture<PlayerInfoDTO> comparePlayerInfoFuture = CompletableFuture.supplyAsync(() -> {
            try {
                PlayerInfoDTO dto = dataExtractor.extractPlayerInfoDTO(params.getComparePlayerName(), params.getMode());
                dto.setAvatar_url(AssetDownloadUtil.avatarAbsolutePath(dto, false));
                return dto;
            } catch (Exception e) {
                throw new LazybotRuntimeException("异步获取玩家" + params.getComparePlayerName() + "数据失败"+ e.getMessage());
            }
        });

        CompletableFuture<ComparePlayerBpList> resultFuture = playerInfoFuture.thenCombineAsync(comparePlayerInfoFuture, (playerInfoDTO, comparePlayerInfoDTO) -> {
            try {
                if (Objects.equals(playerInfoDTO.getId(), comparePlayerInfoDTO.getId())) throw new LazybotRuntimeException("你不能和自己对比");
                CompletableFuture<List<ScoreLazerDTO>> scoreFuture = CompletableFuture.supplyAsync(() ->
                        dataExtractor.extractUserBestScoreList(String.valueOf(playerInfoDTO.getId()), 100, 0, params.getMode()));

                CompletableFuture<List<ScoreLazerDTO>> compareScoreFuture = CompletableFuture.supplyAsync(() ->
                        dataExtractor.extractUserBestScoreList(String.valueOf(comparePlayerInfoDTO.getId()), 100, 0, params.getMode()));

                List<ScoreLazerDTO> scoreDTOS = scoreFuture.get();
                List<ScoreLazerDTO> compareScoreDTOS = compareScoreFuture.get();

                return new ComparePlayerBpList(scoreDTOS, playerInfoDTO, compareScoreDTOS, comparePlayerInfoDTO);
            }
            catch (LazybotRuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new LazybotRuntimeException("[bpvs指令] 异步获取玩家" + params.getPlayerName() + " bp数据失败: "+ e.getMessage());
            }
        });
        return resultFuture.get();
    }


    @Override
    public NoChokeListVO noChoke(GeneralParameter params, int type)
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);

        List<ScoreLazerDTO> originalScoreArray=dataExtractor.extractUserBestAll(
                String.valueOf(playerInfoDTO.getId()), params.getMode());

        NoChokeListVO noChokeListVO=osuToolsUtil.setupNoChokeList(
                OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO),
                TransformerUtil.scoreTransformForList(originalScoreArray),
                type);
        noChokeListVO.setScoreList(noChokeListVO.getScoreList().stream().limit(51).collect(Collectors.toList()));
        return noChokeListVO;
    }
    @Override
    public PlayerInfoVO getPlayerInfoVO(GeneralParameter params) {
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
        playerInfoVO.setMode(params.getMode());
        return playerInfoVO;
    }
    @Override
    public MoelleuxCard cardMoelleux(CardMoelleuxParameter params) throws Exception {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("此样式仅支持osu模式，请输入/card &使用老版样式");
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
        playerInfoVO.setMode(params.getMode());
        PPPlusPerformance performance;
        try{
            performance=dataExtractor.extractPerformancePlusPlayerTotal(playerInfoVO.getId());
        }
        catch (LazybotRuntimeException e) {
            throw new LazybotRuntimeException("Pp+数据获取失败，请稍后再试");
        }
        List<ScoreLazerDTO> scoreDTOS=dataExtractor.extractUserBestScoreList(
                String.valueOf(playerInfoVO.getId()),
                4,
                0,
                params.getMode());
        List<ScoreVO> scoreVOArray = osuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreDTOS));
        PlayerInfoMoelleux playerInfoMoelleux=new PlayerInfoMoelleux(playerInfoVO,
                scoreVOArray,
                performance);
        HSL mainColor = CommonTool.getDominantHSLColorThief(new File(playerInfoVO.getAvatarUrl()));

        boolean isTooDarkOrBright = mainColor.getSaturation()<4 || mainColor.getLightness()>94;
        boolean isLowSaturation = mainColor.getSaturation()<18;
        boolean enableWhiteMask = params.getVersion()!=2;
        if (isTooDarkOrBright) {
            isLowSaturation=false;
        }
        if (params.getVersion()==3) {
            isLowSaturation=false;
        }
        if (params.getVersion()==4) {
            isLowSaturation=true;
        }
        int primaryHue;
        if (params.getOverrideHue()!=null) {
            primaryHue = params.getOverrideHue();
        }
        else{
            primaryHue = isTooDarkOrBright?361:mainColor.getHue();
        }
        return new MoelleuxCard(playerInfoMoelleux, primaryHue, isLowSaturation, enableWhiteMask);
    }

    @Override
    public MoelleuxCard cardMoelleuxTrimmed(CardMoelleuxParameter params) throws Exception {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("此样式仅支持osu模式");
        PlayerInfoDTO player = getTargetPlayerInfoDTO(params);
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(player);
        playerInfoVO.setMode(params.getMode());
        PPPlusPerformance performance;
        try{
            performance=dataExtractor.extractPerformancePlusPlayerTotal(playerInfoVO.getId());
        }
        catch (LazybotRuntimeException e) {
            throw new LazybotRuntimeException("Pp+数据获取失败，请稍后再试");
        }
        playerInfoVO.setBannerUrl(AssetDownloadUtil.bannerAbsolutePath(player,false));
        PlayerInfoMoelleux playerInfoMoelleux=new PlayerInfoMoelleux(playerInfoVO,
                null,
                performance);
        HSL mainColor = CommonTool.getDominantHSLColorThief(new File(playerInfoVO.getAvatarUrl()));

        boolean isTooDarkOrBright = mainColor.getSaturation()<4 || mainColor.getLightness()>94;
        if (isTooDarkOrBright)
        {
            mainColor = CommonTool.getDominantHSLColorThief(new File(playerInfoVO.getAvatarUrl()));
            isTooDarkOrBright = mainColor.getSaturation()<4 || mainColor.getLightness()>94;
            playerInfoVO.setBannerUrl(playerInfoVO.getAvatarUrl());
        }
        int primaryHue;
        if (params.getOverrideHue()!=null) {
            primaryHue = params.getOverrideHue();
        }
        else{
            primaryHue = isTooDarkOrBright?361:mainColor.getHue();
        }
        return new MoelleuxCard(playerInfoMoelleux, primaryHue);
    }


    @Override
    public PerformancePlusProfile getPerformancePlusPlayerInfo(GeneralParameter params)
    {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("Pp+目前仅支持osu模式");
        try{
            PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
            playerInfoVO.setMode(params.getMode());
            if (playerInfoVO.getPrimaryColor()==333) playerInfoVO.setPrimaryColor(208);
            PPPlusPerformance performance=dataExtractor.extractPerformancePlusPlayerTotal(playerInfoVO.getId());

            return new PerformancePlusProfile(performance,playerInfoVO);
        }
        catch (LazybotRuntimeException e) {
            throw e;
        }
        catch (Exception e){
            logger.error("Pp+服务正在维护或生成失败，请稍后再试.params:{}", JSON.toJSONString(params), e);
            throw new LazybotRuntimeException("Pp+服务正在维护或生成失败，请稍后再试");
        }

    }
    @Override
    public PlusScorePerformance getPerformanceDimensionList(PlusListParameter params)
    {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("此功能为PP+相关，仅支持osu模式");
        PlayerInfoDTO player = getTargetPlayerInfoDTO(params);
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(player);

        List<ScorePerformanceDTO> scores;
        ScorePerformanceDimension dimension =  ScorePerformanceDimension.getDimension(params.getDimension());
        try{
            scores=dataExtractor.extractPerformancePlusDimension(playerInfoVO.getId(),
                    dimension,
                    params.getFrom()-1,
                    params.getTo()-params.getFrom()+1
            );
        }
        catch (LazybotRuntimeException e) {
            throw new LazybotRuntimeException("Lazybot-PPplus数据获取失败，请稍后再试");

        }
        PlusScorePerformance playerPerformance=new PlusScorePerformance(scores);
        playerPerformance.setScores(osuToolsUtil.setupScorePerformanceList(playerPerformance.getScores()));
        playerPerformance.setName(playerInfoVO.getPlayerName());
        playerPerformance.setOffset(params.getFrom()-1);
        playerPerformance.setAvatarUrl(playerInfoVO.getAvatarUrl());
        playerPerformance.setDimension(dimension.getShowcase());
        return playerPerformance;
    }


    @Override
    public AddScorePlus addScoreForPerformancePlus(ScoreParameter params)
    {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("Pp+相关操作目前仅支持osu模式");
        try{
            if (params.getPlayerName()!=null) params.setPlayerId(dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode()).getId());
            BeatmapUserScoreLazer beatmapUserScoreLazer = dataExtractor.extractBeatmapUserScore(
                    String.valueOf(params.getBeatmapId()),
                    params.getPlayerId(),
                    params.getMode(),
                    params.getModCombination()
            );
            ScoreVO scoreVO = osuToolsUtil.setupScoreVO(
                    dataExtractor.extractBeatmap(String.valueOf(params.getBeatmapId()), params.getMode()),
                    beatmapUserScoreLazer.getScore(),
                    false);
            LazybotScorePerformance score=dataExtractor.extractPerformancePlusAddScore(params.getPlayerId(),params.getBeatmapId());

            return new AddScorePlus(scoreVO, score);
        }
        catch (LazybotRuntimeException e) {
            throw e;
        }
        catch (Exception e){
            logger.error("成绩添加失败 ", e);
            throw new LazybotRuntimeException("成绩添加失败");
        }
    }

    @Override
    public ProfileInfo profile(ProfileParameter params) throws Exception {
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
        ProfileCustomizationPO customizationPO=customizationMapper.selectById(playerInfoVO.getId());
        playerInfoVO.setMode(params.getMode());
        List<ScoreLazerDTO> scoreDTOS=dataExtractor.extractUserBestScoreList(String.valueOf(playerInfoVO.getId()), 6, 0, params.getMode());
        List<ScoreVO> scoreVOArray= osuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreDTOS));
        playerInfoVO.setBps(scoreVOArray);
        ProfileTheme theme;
        String defaultBackground=ResourceMonitor.getResourcePath().toAbsolutePath()+ "/static/assets/whitespace_" +CommonTool.randomNumberGenerator(3) +".png";
        if (customizationPO!=null) {
            CustomizeServiceImpl.validateProfileCustomizationCache(customizationPO);
            if(customizationPO.getVerified()>0){
                playerInfoVO.setProfileBackgroundUrl(ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/playerCustomization/profile/" + playerInfoVO.getId() +".jpg");
                if(customizationPO.getHue()!=null)
                    theme=ProfileTheme.getInstance(customizationPO.getPreferred_type(),customizationPO.getHue());
                else
                    theme=ProfileTheme.getInstance(customizationPO.getPreferred_type(),CommonTool.getDominantHueColorThief(new File(playerInfoVO.getProfileBackgroundUrl())));
            }
            else {
                playerInfoVO.setProfileBackgroundUrl(defaultBackground);
                theme=ProfileLightTheme.createInstance(192);
            }
        }
        else {
            playerInfoVO.setProfileBackgroundUrl(defaultBackground);
            theme=ProfileLightTheme.createInstance(192);
        }
        AccessTokenPO userToken = tokenMapper.selectByPlayerId(playerInfoVO.getId());
        if (userToken!=null) params.setLazybotId(userToken.getId());
        List<BadgeUserShowcasePO> badges = badgeMapper.selectByUserId(params.getLazybotId());

        return new ProfileInfo(playerInfoVO, theme, badges);
    }

    @Override
    public String nameToId(NameToIdParameter params) {
        StringBuilder builder = new StringBuilder();
        for(String name:params.getTargets()){
            name=name.trim();
            PlayerInfoDTO playerInfoDTO = dataExtractor.extractPlayerInfoDTO(name,params.getMode());
            if(playerInfoDTO.getId()==null){
                builder.append(name).append(" --> ")
                        .append("没这B人\n");
            }
            else {
                builder.append(playerInfoDTO.getUsername()).append(" --> ").append(playerInfoDTO.getId()).append("\n");
            }
        }
        return "[Lazybot] " + builder;
    }
    @Override
    public PlayerScoreList bplistListView(BplistParameter params)
    {
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
        List<ScoreLazerDTO> scoreDTOS=dataExtractor.extractUserBestScoreList(
                String.valueOf(info.getId()),
                params.getTo()-params.getFrom()+1,
                params.getFrom()-1,
                params.getMode());
        List<ScoreSequence> scoreSequences=TransformerUtil.scoreSequenceListTransform(scoreDTOS,false);
        osuToolsUtil.setUpImageStaticSequence(scoreSequences);
        return new PlayerScoreList(info,scoreSequences);
    }

    @Override
    public UserAllScore scoreRank(ScoreParameter params) throws Exception {
        List<AccessTokenPO> users = dataExtractor.extractPlayerInfoByUserIdBatch(params.getGroupUserIds());
        if(CollectionUtils.isEmpty(users)) {
            throw new LazybotRuntimeException("当前群聊没有人绑定账号");
        }
        List<CompletableFuture<MapScore>> futures = users.stream()
                .map(player -> CompletableFuture.supplyAsync(() -> {
                    try {
                        RateLimiterHolder.acquire();

                        ScoreLazerDTO score = dataExtractor.extractBeatmapUserScore(
                                params.getBeatmapId().toString(),
                                player.getPlayer_id(),
                                params.getMode(),
                                params.getModCombination()
                        ).getScore();

                        if (score != null) {
                            MapScore mapScore = TransformerUtil.mapScoreTransform(score);
                            PlayerInfoDTO playerInfoDTO = new PlayerInfoDTO();
                            playerInfoDTO.setId(player.getPlayer_id());
                            playerInfoDTO.setAvatar_url(player.getAvatar_url());
                            OsuToolsUtil.setupPlayerStatics(mapScore, playerInfoDTO);
                            mapScore.setPlayerName(player.getPlayer_name());
                            return mapScore;
                        }
                    } catch (LazybotRuntimeException e) {
                        // 忽略并返回 null
                    } catch (Exception e) {
                        logger.warn("请求失败: {}", e.getMessage());
                    }
                    return null;
                }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR))
                .toList();

        // 等待全部任务完成并收集结果
        List<MapScore> mapScores = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MapScore::getScore).reversed())
                .limit(30)
                .collect(Collectors.toList());


        BeatmapPerformance beatmapPerformance = TransformerUtil.beatmapPerformanceTransform(dataExtractor.extractBeatmap(String.valueOf(params.getBeatmapId()),params.getMode()));
        // 调用svg渲染
        mapScores = setupMapScores(mapScores, beatmapPerformance, Comparator.comparing(MapScore::getScore), beatmapPerformance.getChecksum());
        return new UserAllScore(mapScores, beatmapPerformance);
    }

    @NotNull
    private List<MapScore> setupMapScores(List<MapScore> mapScores, BeatmapPerformance beatmapPerformance, Comparator<MapScore> comparing, String checksum) throws IOException
    {
        JniBeatmap beatmap=new JniBeatmap(Files.readAllBytes(AssetDownloadUtil.beatmapPath(beatmapPerformance.getBid(),false)));
        beatmapPerformance.setDifficultyAttributes(RosuUtil.nomodMapStats(beatmap, beatmapPerformance.getMode().getDescribe()));
        beatmapPerformance.setBgUrl(assetDownloader.beatmapBackgroundAbsolutePath(beatmapPerformance.getSid()));
        beatmapPerformance.setLengthBonus(CommonTool.lengthBonusCalc(beatmapPerformance.getCountCircles()+beatmapPerformance.getCountSliders()+beatmapPerformance.getCountSpinners()));
        for (MapScore mapScore:mapScores) {
            try {
                RosuUtil.setupMapScorePerformance(beatmap, mapScore);
                mapScore.setupBpm(mapScore,beatmapPerformance);
            }
            catch (Exception e) {
                logger.error(e.getMessage());
                throw new LazybotRuntimeException("Error during recalculations/重算成绩时出错: " + e.getMessage());
            }
        }
        mapScores=mapScores.stream().sorted(comparing.reversed()).toList();
        verifyBeatmapsCache(beatmapPerformance.getBid(), checksum);
        return mapScores;
    }


    private boolean verifyBeatmapsCache(ScoreVO scoreVO) {
       return verifyBeatmapsCache(scoreVO.getBeatmap().getBid(),scoreVO.getBeatmap().getChecksum());
    }
    private boolean verifyBeatmapsCache(Integer bid, String checksum) {
        String checksum2=CommonTool.calculateMD5(new File(AssetDownloadUtil.beatmapPath(bid,false).toUri()));
        if (!checksum2.equals(checksum)) {
            logger.warn("Checksum mismatch, downloading beatmap: {} != {}", checksum2, checksum);
            AssetDownloadUtil.beatmapPath(bid, true);
            return false;
        }
        logger.info("地图哈希值匹配正常: {}", checksum);
        return true;
    }


    private PlayerInfoDTO getTargetPlayerInfoDTO(LazybotCommandParameter params)
    {
        PlayerInfoDTO playerInfoDTO;
        if (params.getPlayerName()==null || params.getPlayerName().isEmpty()) playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerId(),params.getMode());
        else playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerName().trim(),params.getMode());
        return playerInfoDTO;
    }

    private PPPlusScore setupPlusScore(ScoreVO scoreVO) throws RosuFFI.FFIException
    {
        PPPlusScore scorePlus = new PPPlusScore(scoreVO);
        scorePlus.setPlusPerformance(PlusPPUtil.calcPPPlusStats(String.valueOf(AssetDownloadUtil.beatmapPath(scoreVO,false).toAbsolutePath()),scoreVO));
        scorePlus.setMaxPerformance(PlusPPUtil.calcMaxPPPlusStats(String.valueOf(AssetDownloadUtil.beatmapPath(scoreVO,false).toAbsolutePath()),scoreVO));
        return scorePlus;
    }




}
