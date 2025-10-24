package me.aloic.lazybot.osu.service.ServiceImpl;

import com.alibaba.fastjson.JSON;
import desu.life.RosuFFI;
import jakarta.annotation.Resource;
import me.aloic.lazybot.entity.po.BadgeDefinitionPO;
import me.aloic.lazybot.entity.po.BadgeUserOwnedPO;
import me.aloic.lazybot.entity.po.BadgeUserShowcasePO;
import me.aloic.lazybot.entity.vo.BadgeUserVO;
import me.aloic.lazybot.entity.vo.ThumbnailClassicalVO;
import me.aloic.lazybot.exception.LazybotNotFoundException;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.mapping.documentMapper.*;
import me.aloic.lazybot.graphics.render.SVGRenderer;
import me.aloic.lazybot.monitor.CompareMonitor;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotScorePerformance;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import me.aloic.lazybot.osu.dao.entity.vo.*;
import me.aloic.lazybot.osu.dao.mapper.*;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.theme.Color.HSL;
import me.aloic.lazybot.osu.theme.preset.ProfileLightTheme;
import me.aloic.lazybot.osu.theme.preset.ProfileTheme;
import me.aloic.lazybot.osu.utils.*;
import me.aloic.lazybot.parameter.*;
import me.aloic.lazybot.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.osu.extended.rosu.JniBeatmap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    @Autowired
    private TokenMapper tokenMapper;


    @Override
    public byte[] score(ScoreParameter params) throws Exception
    {
        ScoreVO scoreVO = processScoreScore(params);
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreSVGMapper.renderScoreToImage(scoreVO, params.getVersion(), getDominantColorArray(scoreVO))
        );
    }
    @Override
    public byte[] scorePlus(ScoreParameter params) throws Exception
    {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("QuadraGrid样式仅支持Std模式，因为其他模式没有PP+数据");
        ScoreVO scoreVO = processScoreScore(params);
        PPPlusScore scorePlus = new PPPlusScore(scoreVO);
        scorePlus.setPlusPerformance(PlusPPUtil.calcPPPlusStats(String.valueOf(AssertDownloadUtil.beatmapPath(scoreVO,false).toAbsolutePath()),scoreVO));
        scorePlus.setMaxPerformance(PlusPPUtil.calcMaxPPPlusStats(String.valueOf(AssertDownloadUtil.beatmapPath(scoreVO,false).toAbsolutePath()),scoreVO));
        return SVGRenderer.renderSVGDocumentToByteArray(
                PlusScoreSVGMapper.mapPlusScoreToQuadraGrid(scorePlus,CommonTool.getDominantHueColorThief(new File(scoreVO.getBeatmap().getBgUrl())))
        );
    }
    private ScoreVO processScoreScore(ScoreParameter params)
    {
        if (params.getPlayerName()!=null) params.setPlayerId(dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode()).getId());
        BeatmapUserScoreLazer beatmapUserScoreLazer = dataExtractor.extractBeatmapUserScore(
                String.valueOf(params.getBeatmapId()),
                params.getPlayerId(),
                params.getMode(),
                params.getModCombination()
        );
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(params.getBeatmapId()), params.getMode()),
                beatmapUserScoreLazer.getScore(),
                false);
        verifyBeatmapsCache(scoreVO);
        if (params.getChannelId()!=null && params.getChannelId()!=1919810L)
            CompareMonitor.saveRecentBeatmap(params.getChannelId(), scoreVO.getBeatmap().getBid());
        return scoreVO;
    }




    @Override
    public byte[] allScore(ScoreParameter params) throws Exception {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);

        List<ScoreLazerDTO> scoreList = dataExtractor.extractBeatmapUserScoreAll(params.getBeatmapId(), playerInfoDTO.getId(), params.getMode());
        if (scoreList==null || scoreList.isEmpty()) throw new LazybotRuntimeException("没有找到" + playerInfoDTO.getUsername() +"在" + params.getBeatmapId()+ "上的成绩");
        List<MapScore> mapScoreList=TransformerUtil.mapScoreTransform(scoreList);

        OsuToolsUtil.setupPlayerStatics(mapScoreList,playerInfoDTO);
        BeatmapDTO beatmapDTO = dataExtractor.extractBeatmap(String.valueOf(params.getBeatmapId()),params.getMode());
        BeatmapPerformance beatmapPerformance=TransformerUtil.beatmapPerformanceTransform(beatmapDTO);
        JniBeatmap beatmap=new JniBeatmap(Files.readAllBytes(AssertDownloadUtil.beatmapPath(beatmapPerformance.getBid(),false)));
        beatmapPerformance.setDifficultyAttributes(RosuUtil.nomodMapStats(beatmap, beatmapPerformance.getMode().getDescribe()));
        beatmapPerformance.setBgUrl(AssertDownloadUtil.svgAbsolutePath(beatmapPerformance.getSid()));
        beatmapPerformance.setLengthBonus(CommonTool.lengthBonusCalc(beatmapPerformance.getCountCircles()+beatmapPerformance.getCountSliders()+beatmapPerformance.getCountSpinners()));
        for (MapScore mapScore:mapScoreList) {
                try {
                    RosuUtil.setupMapScorePerformance(beatmap, mapScore);
                    mapScore.setupBpm(mapScore,beatmapPerformance);
                }
                catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new LazybotRuntimeException("Error during recalculations/重算成绩时出错: " + e.getMessage());
                }
        }
        mapScoreList=mapScoreList.stream().sorted(Comparator.comparing(MapScore::getPp).reversed()).toList();
        verifyBeatmapsCache(beatmapPerformance.getBid(), beatmapDTO.getChecksum());
        if (params.getChannelId()!=null && params.getChannelId()!=1919810L)
            CompareMonitor.saveRecentBeatmap(params.getChannelId(), params.getBeatmapId());
        return SVGRenderer.renderSVGDocumentToByteArray(
                MapScoreSVGMapper.mapMapScoreListToAllScorePanel(mapScoreList,beatmapPerformance, false),
                2f);
    }
    @Override
    public byte[] thumbnailClassicalScore(ThumbnailParameter params)
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);
        List<ScoreLazerDTO> scoreList = dataExtractor.extractBeatmapUserScoreAll(params.getBeatmapId(), playerInfoDTO.getId(), params.getMode());
        if (scoreList==null || scoreList.isEmpty()) throw new LazybotRuntimeException("没有找到" + playerInfoDTO.getUsername() +"在" + params.getBeatmapId()+ "上的成绩");
        scoreList.get(params.getIndex()-1).setUser(playerInfoDTO);
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(params.getBeatmapId()), params.getMode()),
                scoreList.get(params.getIndex()-1),
                false);
        if (!scoreVO.getIsLazer())
        {
            scoreVO.setModJSON(scoreVO.getModJSON().stream().filter(mod -> !mod.getAcronym().equals("CL")).toList());
        }
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        ThumbnailClassicalVO tbc = new ThumbnailClassicalVO(info,scoreVO,params.getComment(),
                params.getPosition()==null ? null:String.valueOf(params.getPosition())
        );
        if (params.getAttributes()!=null && !params.getAttributes().isEmpty())
        {
            tbc.setAttributes(params.getAttributes());
        }
        return SVGRenderer.renderSVGDocumentToByteArray(
                ThumbnailSVGMapper.mapToThumbnailClassical(tbc));
    }
    @Override
    public byte[] thumbnailClassicalRecent(ThumbnailParameter params)
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);
        List<ScoreLazerDTO> scoreList = dataExtractor.extractRecentScoreList(playerInfoDTO.getId(), 1, params.getIndex(), params.getMode());
        if(params.getIndex()>scoreList.size()) {
            throw new LazybotRuntimeException("超出能索引的最大距离，当前为: "+params.getIndex()+", 最大为: " + scoreList.size());
        }
        scoreList.get(params.getIndex()-1).setUser(playerInfoDTO);
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(scoreList.get(params.getIndex() - 1).getBeatmap_id()), params.getMode()),
                scoreList.get(params.getIndex()-1),
                false);
        if (!scoreVO.getIsLazer())
        {
            scoreVO.setModJSON(scoreVO.getModJSON().stream().filter(mod -> !mod.getAcronym().equals("CL")).toList());
        }
        verifyBeatmapsCache(scoreVO);
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        ThumbnailClassicalVO tbc = new ThumbnailClassicalVO(info,scoreVO,params.getComment(),
                params.getPosition()==null ? null:String.valueOf(params.getPosition())
        );
        if (params.getAttributes()!=null && !params.getAttributes().isEmpty())
        {
            tbc.setAttributes(params.getAttributes());
        }
        return SVGRenderer.renderSVGDocumentToByteArray(
                ThumbnailSVGMapper.mapToThumbnailClassical(tbc));
    }

    @Override
    public byte[] recent(RecentParameter params, int type) throws IOException
    {
        ScoreVO scoreVO = processRecentScore(params, type);
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreSVGMapper.renderScoreToImage(scoreVO, params.getVersion(), getDominantColorArray(scoreVO))
        );
    }
    private ScoreVO processRecentScore(RecentParameter params, int type)
    {
        if (params.getPlayerName()!=null) params.setPlayerId(dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode()).getId());
        List<ScoreLazerDTO> scoreList = dataExtractor.extractRecentScoreList(params.getPlayerId(), type, params.getIndex(), params.getMode());
        if(params.getIndex()>scoreList.size()) {
            throw new LazybotRuntimeException("当前超出能索引的最大距离，当前为: "+params.getIndex());
        }
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(scoreList.get(params.getIndex() - 1).getBeatmap_id()), params.getMode()),
                scoreList.get(params.getIndex() - 1),
                false);
        verifyBeatmapsCache(scoreVO);
        if (params.getChannelId()!=null)
            CompareMonitor.saveRecentBeatmap(params.getChannelId(), scoreVO.getBeatmap().getBid());
        return scoreVO;
    }

    @Override
    public byte[] recentPlus(RecentParameter params, int type) throws IOException, RosuFFI.FFIException
    {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("QuadraGrid样式仅支持Std模式，因为其他模式没有PP+数据");
        ScoreVO scoreVO = processRecentScore(params, type);
        PPPlusScore scorePlus = new PPPlusScore(scoreVO);
        scorePlus.setPlusPerformance(PlusPPUtil.calcPPPlusStats(String.valueOf(AssertDownloadUtil.beatmapPath(scoreVO,false).toAbsolutePath()),scoreVO));
        scorePlus.setMaxPerformance(PlusPPUtil.calcMaxPPPlusStats(String.valueOf(AssertDownloadUtil.beatmapPath(scoreVO,false).toAbsolutePath()),scoreVO));

        return SVGRenderer.renderSVGDocumentToByteArray(
                PlusScoreSVGMapper.mapPlusScoreToQuadraGrid(scorePlus,CommonTool.getDominantHueColorThief(new File(scoreVO.getBeatmap().getBgUrl())))
        );

    }
    @Override
    public byte[] bp(BpParameter params) throws IOException
    {
        ScoreVO scoreVO = processBpScore(params);
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreSVGMapper.renderScoreToImage(scoreVO, params.getVersion(), getDominantColorArray(scoreVO))
        );
    }
    private ScoreVO processBpScore(BpParameter params)
    {
        if (params.getPlayerName()!=null) params.setPlayerId(dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode()).getId());
        List<ScoreLazerDTO> scoreDTO = dataExtractor.extractUserBestScoreList(
                String.valueOf(params.getPlayerId()),
                params.getIndex()-1,
                params.getMode());

        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(scoreDTO.getFirst().getBeatmap_id()),params.getMode()),
                scoreDTO.getFirst(),
                false);
        verifyBeatmapsCache(scoreVO);
        CompareMonitor.saveRecentBeatmap(params.getChannelId(), scoreVO.getBeatmap().getBid());
        return scoreVO;
    }
    @Override
    public byte[] bpPlus(BpParameter params) throws IOException, RosuFFI.FFIException
    {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("QuadraGrid样式仅支持Std模式，因为其他模式没有PP+数据");
        ScoreVO scoreVO = processBpScore(params);
        PPPlusScore scorePlus = new PPPlusScore(scoreVO);
        scorePlus.setPlusPerformance(PlusPPUtil.calcPPPlusStats(String.valueOf(AssertDownloadUtil.beatmapPath(scoreVO,false).toAbsolutePath()),scoreVO));
        scorePlus.setMaxPerformance(PlusPPUtil.calcMaxPPPlusStats(String.valueOf(AssertDownloadUtil.beatmapPath(scoreVO,false).toAbsolutePath()),scoreVO));
        return SVGRenderer.renderSVGDocumentToByteArray(
                PlusScoreSVGMapper.mapPlusScoreToQuadraGrid(scorePlus,CommonTool.getDominantHueColorThief(new File(scoreVO.getBeatmap().getBgUrl())))
        );
    }
    @Override
    public byte[] bplistCardView(BplistParameter params) throws Exception
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        List<ScoreLazerDTO> scoreDTOS=dataExtractor.extractUserBestScoreList(
                String.valueOf(playerInfoDTO.getId()),
                params.getTo()-params.getFrom()+1,
                params.getFrom()-1,
                params.getMode());
        List<ScoreVO> scoreVOArray= OsuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreDTOS));
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreListSVGMapper.mapScoreListToBpCard(info,scoreVOArray,params.getFrom(),1)
        );
    }


    @Override
    public byte[] bpScoreFilter(ScoreFilterParameter params) throws Exception
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
        OsuToolsUtil.setUpImageStatic(scoreVOList);
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreListSVGMapper.mapScoreListToBpCard(info,scoreVOList,1,4,
                        "Current Command: /Filter, get desired best performances with given statements.")
        );
    }

    @Override
    public byte[] playRecentSeries(GeneralParameter params, int type, int style) throws IOException
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        List<ScoreLazerDTO> scoreDTOS= dataExtractor.extractRecentScoreList(
                info.getId(),
                type,
                21,
                params.getMode());
        if (style==0)
        {
            List<ScoreVO> scoreVOArray = OsuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreDTOS));
            return SVGRenderer.renderSVGDocumentToByteArray(
                    ScoreListSVGMapper.mapScoreListToBpCard(info, scoreVOArray, 1, 1)
            );
        }
        else {
            List<ScoreSequence> scoreSequences=TransformerUtil.scoreSequenceListTransform(scoreDTOS,true);
            OsuToolsUtil.setUpImageStaticSequence(scoreSequences);
            return SVGRenderer.renderSVGDocumentToByteArray(
                    ScoreListSVGMapper.mapScoreListToBpList(scoreSequences,info,1)
            );
        }
    }

    @Override
    public byte[] todayBp(TodaybpParameter params) throws Exception
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

        OsuToolsUtil.setUpImageStatic(scoreVOList);
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreListSVGMapper.mapScoreListToBpCard(info,scoreVOList,0,4,
                "Current command: /todayBp. Showing new Bps within " + params.getMaxDays() +" day(s)")
        );
    }
    @Override
    public byte[] bpvs(BpvsParameter params)throws Exception
    {
        CompletableFuture<PlayerInfoDTO> playerInfoFuture = CompletableFuture.supplyAsync(() -> {
            try {
                PlayerInfoDTO dto;
                if (params.getPlayerName()!=null) dto= dataExtractor.extractPlayerInfoDTO(params.getPlayerName(), params.getMode());
                else dto= dataExtractor.extractPlayerInfoDTO(params.getPlayerId(), params.getMode());
                dto.setAvatar_url(AssertDownloadUtil.avatarAbsolutePath(dto, false));
                return dto;
            } catch (Exception e) {
                throw new LazybotRuntimeException("异步获取玩家" + params.getPlayerName() + "数据失败"+ e.getMessage());
            }
        });

        CompletableFuture<PlayerInfoDTO> comparePlayerInfoFuture = CompletableFuture.supplyAsync(() -> {
            try {
                PlayerInfoDTO dto = dataExtractor.extractPlayerInfoDTO(params.getComparePlayerName(), params.getMode());
                dto.setAvatar_url(AssertDownloadUtil.avatarAbsolutePath(dto, false));
                return dto;
            } catch (Exception e) {
                throw new LazybotRuntimeException("异步获取玩家" + params.getComparePlayerName() + "数据失败"+ e.getMessage());
            }
        });

        CompletableFuture<byte[]> resultFuture = playerInfoFuture.thenCombineAsync(comparePlayerInfoFuture, (playerInfoDTO, comparePlayerInfoDTO) -> {
            try {
                if (Objects.equals(playerInfoDTO.getId(), comparePlayerInfoDTO.getId())) throw new LazybotRuntimeException("你不能和自己对比");
                CompletableFuture<List<ScoreLazerDTO>> scoreFuture = CompletableFuture.supplyAsync(() ->
                        dataExtractor.extractUserBestScoreList(String.valueOf(playerInfoDTO.getId()), 100, 0, params.getMode()));

                CompletableFuture<List<ScoreLazerDTO>> compareScoreFuture = CompletableFuture.supplyAsync(() ->
                        dataExtractor.extractUserBestScoreList(String.valueOf(comparePlayerInfoDTO.getId()), 100, 0, params.getMode()));

                List<ScoreLazerDTO> scoreDTOS = scoreFuture.get();
                List<ScoreLazerDTO> compareScoreDTOS = compareScoreFuture.get();

                return SVGRenderer.renderSVGDocumentToByteArray(
                        CompareScoreListSVGMapper.mapScoresToCompareScoreList(
                                playerInfoDTO,
                                comparePlayerInfoDTO,
                                TransformerUtil.scoreTransformForArray(scoreDTOS),
                                TransformerUtil.scoreTransformForArray(compareScoreDTOS)
                        )
                );
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
    public byte[] noChoke(GeneralParameter params, int type) throws Exception
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);

        List<ScoreLazerDTO> originalScoreArray=dataExtractor.extractUserBestAll(
                String.valueOf(playerInfoDTO.getId()), params.getMode());

        NoChokeListVO noChokeListVO=OsuToolsUtil.setupNoChokeList(
                OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO),
                TransformerUtil.scoreTransformForList(originalScoreArray),
                type);
        noChokeListVO.setScoreList(noChokeListVO.getScoreList().stream().limit(51).collect(Collectors.toList()));
        if(type==1) {
            return SVGRenderer.renderSVGDocumentToByteArray(
                    ScoreListSVGMapper.mapScoreListToBpCard(noChokeListVO.getInfo(),noChokeListVO.getScoreList(),0,2)
            );
        }
        else {
            return SVGRenderer.renderSVGDocumentToByteArray(
                    ScoreListSVGMapper.mapScoreListToBpCard(noChokeListVO.getInfo(),noChokeListVO.getScoreList(),0,3,
                    "All scores are recalculated with FC. Plz keep in mind that this may not reflect your skill correctly.")
            );
        }
    }
    @Override
    public byte[] card(GeneralParameter params) throws Exception {
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
        playerInfoVO.setMode(params.getMode());
        return SVGRenderer.renderSVGDocumentToByteArray(
                PlayerInfoSVGMapper.mapPlayerInfoToCard(playerInfoVO)
        );
    }
    @Override
    public byte[] cardMoelleux(CardMoelleuxParameter params) throws Exception {
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
        List<ScoreVO> scoreVOArray = OsuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreDTOS));
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
        return SVGRenderer.renderSVGDocumentToByteArray(
                PlayerInfoSVGMapper.mapPlayerInfoMoelleuxToCard(playerInfoMoelleux, primaryHue, isLowSaturation, enableWhiteMask)
                ,2
        );
    }

    @Override
    public byte[] cardMoelleuxTrimmed(CardMoelleuxParameter params) throws Exception {
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
        playerInfoVO.setBannerUrl(AssertDownloadUtil.bannerAbsolutePath(player,false));
        PlayerInfoMoelleux playerInfoMoelleux=new PlayerInfoMoelleux(playerInfoVO,
                null,
                performance);
        HSL mainColor = CommonTool.getDominantHSLColorThief(new File(playerInfoVO.getBannerUrl()));

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
        return SVGRenderer.renderSVGDocumentToByteArrayPNG(
                PlayerInfoSVGMapper.mapPlayerInfoMoelleuxToCardTrimmed(playerInfoMoelleux, primaryHue)
                ,1
        );
    }
    @Override
    public byte[] performancePlus(GeneralParameter params)
    {
        if (!Objects.equals(params.getMode(), "osu")) throw new LazybotRuntimeException("Pp+目前仅支持osu模式");
        try{
            PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
            playerInfoVO.setMode(params.getMode());
            if (playerInfoVO.getPrimaryColor()==333) playerInfoVO.setPrimaryColor(208);
            PPPlusPerformance performance=dataExtractor.extractPerformancePlusPlayerTotal(playerInfoVO.getId());

            if (params.getVersion()==1) return SVGRenderer.renderSVGDocumentToByteArray(
                    PlusCardSVGMapper.mapPlusInfoToCardCC2024(performance,playerInfoVO),
                    1);
            return SVGRenderer.renderSVGDocumentToByteArray(
                    PlusCardSVGMapper.mapPlusInfoToCard(performance,playerInfoVO),
                    2);
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
    public byte[] addScoreForPerformancePlus(ScoreParameter params)
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
            ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(
                    dataExtractor.extractBeatmap(String.valueOf(params.getBeatmapId()), params.getMode()),
                    beatmapUserScoreLazer.getScore(),
                    false);
            LazybotScorePerformance score=dataExtractor.extractPerformancePlusAddScore(params.getPlayerId(),params.getBeatmapId());

            return SVGRenderer.renderSVGDocumentToByteArray(
                    PlusScoreSVGMapper.mapPlusScoreToCard(score, scoreVO, CommonTool.rgbToHue(getDominantColorArray(scoreVO))
                    ));
        }
        catch (LazybotRuntimeException e) {
            throw e;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new LazybotRuntimeException("成绩添加失败");
        }

    }

    @Override
    public byte[] profile(ProfileParameter params) throws Exception {
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
        ProfileCustomizationPO customizationPO=customizationMapper.selectById(playerInfoVO.getId());
        playerInfoVO.setMode(params.getMode());
        List<ScoreLazerDTO> scoreDTOS=dataExtractor.extractUserBestScoreList(String.valueOf(playerInfoVO.getId()), 6, 0, params.getMode());
        List<ScoreVO> scoreVOArray= OsuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreDTOS));
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

        return SVGRenderer.renderSVGDocumentToByteArray(
                PlayerInfoSVGMapper.mapPlayerInfoToProfilePanel(playerInfoVO, theme, badges)
        );
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

        return "[Lazybot] " + builder.toString();
    }
    @Override
    public byte[] bplistListView(BplistParameter params) throws Exception
    {
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
        List<ScoreLazerDTO> scoreDTOS=dataExtractor.extractUserBestScoreList(
                String.valueOf(info.getId()),
                params.getTo()-params.getFrom()+1,
                params.getFrom()-1,
                params.getMode());
        List<ScoreSequence> scoreSequences=TransformerUtil.scoreSequenceListTransform(scoreDTOS,false);
        OsuToolsUtil.setUpImageStaticSequence(scoreSequences);
        return SVGRenderer.renderSVGDocumentToByteArray(
                ScoreListSVGMapper.mapScoreListToBpList(scoreSequences,info,params.getFrom())
        );
    }

    @Override
    public byte[] scoreRank(ScoreParameter params) throws Exception {
        List<AccessTokenPO> users = dataExtractor.extractPlayerInfoByUserIdBatch(params.getGroupUserIds());
        if(CollectionUtils.isEmpty(users)) {
            throw new LazybotRuntimeException("当前群聊没有人绑定账号");
        }
        // 挨个查询每个用户在当前beatmap的最好成绩score
//        List<MapScore> mapScores = new ArrayList<>();
//        for(AccessTokenPO player : users) {
//            try {
//                ScoreLazerDTO score = dataExtractor.extractBeatmapUserScore(
//                        params.getBeatmapId().toString(),
//                        player.getPlayer_id(), params.getMode(),
//                        params.getModCombination())
//                        .getScore();
//                MapScore mapScore = TransformerUtil.mapScoreTransform(score);
//                PlayerInfoDTO playerInfoDTO = new PlayerInfoDTO();
//                playerInfoDTO.setId(player.getPlayer_id());
//                playerInfoDTO.setAvatar_url(player.getAvatar_url());
//                OsuToolsUtil.setupPlayerStatics(mapScore, playerInfoDTO);
//                mapScore.setPlayerName(player.getPlayer_name());
//                mapScores.add(mapScore);
//            }catch (LazybotRuntimeException e) {
//                continue;
//            }
//        }
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
        JniBeatmap beatmap=new JniBeatmap(Files.readAllBytes(AssertDownloadUtil.beatmapPath(beatmapPerformance.getBid(),false)));
        beatmapPerformance.setDifficultyAttributes(RosuUtil.nomodMapStats(beatmap, beatmapPerformance.getMode().getDescribe()));
        beatmapPerformance.setBgUrl(AssertDownloadUtil.svgAbsolutePath(beatmapPerformance.getSid()));
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
        mapScores=mapScores.stream().sorted(Comparator.comparing(MapScore::getScore).reversed()).limit(30).toList();
        verifyBeatmapsCache(beatmapPerformance.getBid(), beatmapPerformance.getChecksum());
        return SVGRenderer.renderSVGDocumentToByteArray(
                MapScoreSVGMapper.mapMapScoreListToAllScorePanel(mapScores,beatmapPerformance, true),
                2f);
    }


    private boolean verifyBeatmapsCache(ScoreVO scoreVO) {
       return verifyBeatmapsCache(scoreVO.getBeatmap().getBid(),scoreVO.getBeatmap().getChecksum());
    }
    private boolean verifyBeatmapsCache(Integer bid, String checksum) {
        String checksum2=CommonTool.calculateMD5(new File(AssertDownloadUtil.beatmapPath(bid,false).toUri()));
        if (!checksum2.equals(checksum)) {
            logger.warn("Checksum mismatch, downloading beatmap: {} != {}", checksum2, checksum);
            AssertDownloadUtil.beatmapPath(bid, true);
            return false;
        }
        logger.info("地图哈希值匹配正常: {}", checksum);
        return true;
    }

    private int[] getDominantColorArray(ScoreVO scoreVO) throws IOException {
        return CommonTool.getDominantColorColorThief(new File(scoreVO.getBeatmap().getBgUrl()));
    }
    private PlayerInfoDTO getTargetPlayerInfoDTO(LazybotCommandParameter params)
    {
        PlayerInfoDTO playerInfoDTO;
        if (params.getPlayerName()==null) playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerId(),params.getMode());
        else playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerName().trim(),params.getMode());
        return playerInfoDTO;
    }

    @Override
    public byte[] avatar(GeneralParameter params, int type) throws Exception {
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
        playerInfoVO.setMode(params.getMode());
        if (type==1)
            return SVGRenderer.renderSVGDocumentToByteArray(
                    AvatarSVGMapper.mapPlayerInfoToAvatar(playerInfoVO,
                            CommonTool.getDominantHueColorThief(new File(playerInfoVO.getAvatarUrl())),
                    type)
            );
        else
            return SVGRenderer.renderSVGDocumentToByteArray(
                    AvatarSVGMapper.mapPlayerInfoToAvatar(playerInfoVO,
                            215,
                            type));
    }

}
