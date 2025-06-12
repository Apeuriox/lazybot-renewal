package me.aloic.lazybot.osu.service.ServiceImpl;

import com.alibaba.fastjson2.TypeReference;
import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.vo.*;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.theme.preset.ProfileLightTheme;
import me.aloic.lazybot.osu.theme.preset.ProfileTheme;
import me.aloic.lazybot.osu.utils.*;
import me.aloic.lazybot.parameter.*;
import me.aloic.lazybot.util.*;
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


    @Override
    public byte[] score(ScoreParameter params) throws Exception
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
            return SVGRenderUtil.renderScoreToByteArray(scoreVO, params.getVersion(), getDominantColorArray(scoreVO));
    }
    @Override
    public byte[] allScore(ScoreParameter params) throws Exception
    {
        PlayerInfoDTO playerInfoDTO = getTargetPlayerInfoDTO(params);

        List<ScoreLazerDTO> scoreList = dataExtractor.extractBeatmapUserScoreAll(params.getBeatmapId(), playerInfoDTO.getId(), params.getMode());
        if (scoreList==null || scoreList.isEmpty()) throw new LazybotRuntimeException("[Lazybot] 没有找到" + playerInfoDTO.getUsername() +"在" + params.getBeatmapId()+ "上的成绩");
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
                    throw new LazybotRuntimeException("[Lazybot] Error during recalculations/重算成绩时出错: " + e.getMessage());
                }
        }
        mapScoreList=mapScoreList.stream().sorted(Comparator.comparing(MapScore::getPp).reversed()).toList();
        verifyBeatmapsCache(beatmapPerformance.getBid(), beatmapDTO.getChecksum());
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createMapScoreList(mapScoreList,beatmapPerformance),2f);
    }

    @Override
    public byte[] recent(RecentParameter params, Integer type) throws IOException
    {
        if (params.getPlayerName()!=null) params.setPlayerId(dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode()).getId());
        List<ScoreLazerDTO> scoreList = dataExtractor.extractRecentScoreList(params.getPlayerId(), type, params.getIndex(), params.getMode());
        if(params.getIndex()>scoreList.size()) {
            throw new LazybotRuntimeException("[Lazybot] 超出能索引的最大距离，当前为: "+params.getIndex()+", 最大为: " + scoreList.size());
        }
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(
                dataExtractor.extractBeatmap(String.valueOf(scoreList.get(params.getIndex() - 1).getBeatmap_id()), params.getMode()),
                scoreList.get(params.getIndex() - 1),
                false);
        verifyBeatmapsCache(scoreVO);
        return SVGRenderUtil.renderScoreToByteArray(scoreVO, params.getVersion(), getDominantColorArray(scoreVO));

    }
    @Override
    public byte[] bp(BpParameter params) throws IOException
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
        return SVGRenderUtil.renderScoreToByteArray(scoreVO,params.getVersion(), getDominantColorArray(scoreVO));
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
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createBpCard(info,scoreVOArray,params.getFrom(),1));
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
        if(scoreVOList.isEmpty()) throw new LazybotRuntimeException("[Lazybot] 没有找到符合条件的bp");

        OsuToolsUtil.setUpImageStatic(scoreVOList);
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createBpCard(info,scoreVOList,0,4,
                "Current command: /todayBp. Showing new Bps within " + params.getMaxDays() +" day(s)"));
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
                throw new LazybotRuntimeException("[Lazybot] 异步获取玩家" + params.getPlayerName() + "数据失败"+ e.getMessage());
            }
        });

        CompletableFuture<PlayerInfoDTO> comparePlayerInfoFuture = CompletableFuture.supplyAsync(() -> {
            try {
                PlayerInfoDTO dto = dataExtractor.extractPlayerInfoDTO(params.getComparePlayerName(), params.getMode());
                dto.setAvatar_url(AssertDownloadUtil.avatarAbsolutePath(dto, false));
                return dto;
            } catch (Exception e) {
                throw new LazybotRuntimeException("[Lazybot] 异步获取玩家" + params.getComparePlayerName() + "数据失败"+ e.getMessage());
            }
        });

        CompletableFuture<byte[]> resultFuture = playerInfoFuture.thenCombineAsync(comparePlayerInfoFuture, (playerInfoDTO, comparePlayerInfoDTO) -> {
            try {
                if (Objects.equals(playerInfoDTO.getId(), comparePlayerInfoDTO.getId())) throw new LazybotRuntimeException("[Lazybot] 你不能和自己对比");
                CompletableFuture<List<ScoreLazerDTO>> scoreFuture = CompletableFuture.supplyAsync(() ->
                        dataExtractor.extractUserBestScoreList(String.valueOf(playerInfoDTO.getId()), 100, 0, params.getMode()));

                CompletableFuture<List<ScoreLazerDTO>> compareScoreFuture = CompletableFuture.supplyAsync(() ->
                        dataExtractor.extractUserBestScoreList(String.valueOf(comparePlayerInfoDTO.getId()), 100, 0, params.getMode()));

                List<ScoreLazerDTO> scoreDTOS = scoreFuture.get();
                List<ScoreLazerDTO> compareScoreDTOS = compareScoreFuture.get();

                return SVGRenderUtil.renderSVGDocumentToByteArray(
                        SvgUtil.createCompareBpList(
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

        List<ScoreLazerDTO> originalScoreArray=dataExtractor.extractUserBestScoreList(
                String.valueOf(playerInfoDTO.getId()),
                100,0,params.getMode());

        NoChokeListVO noChokeListVO=OsuToolsUtil.setupNoChokeList(
                OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO),
                TransformerUtil.scoreTransformForList(originalScoreArray),
                type);

        if(type==1) {
            return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createBpCard(noChokeListVO.getInfo(),noChokeListVO.getScoreList(),0,2));
        }
        else {
            return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createBpCard(noChokeListVO.getInfo(),noChokeListVO.getScoreList(),0,3,
                    "All scores are recalculated with FC. Plz keep in mind that this may not reflect your skill correctly."));
        }
    }
    @Override
    public byte[] card(GeneralParameter params) throws Exception {
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
        playerInfoVO.setMode(params.getMode());
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createInfoCard(playerInfoVO));
    }
    @Override
    public byte[] performancePlus(GeneralParameter params)
    {
        try{
            PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
            playerInfoVO.setMode(params.getMode());
            if (playerInfoVO.getPrimaryColor()==333) playerInfoVO.setPrimaryColor(208);
            PPPlusPerformance performance=dataExtractor.extractPerformancePlusPlayerTotal(playerInfoVO.getId());
            return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createPPPlusPanel(performance,playerInfoVO),2);
        }
        catch (Exception e){
            throw new LazybotRuntimeException("[Lazybot] Pp+服务正在维护或生成失败，请稍后再试");
        }

    }

    @Override
    public byte[] profile(ProfileParameter params) throws Exception {

        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(getTargetPlayerInfoDTO(params));
        playerInfoVO.setMode(params.getMode());
        List<ScoreLazerDTO> scoreDTOS=dataExtractor.extractUserBestScoreList(String.valueOf(playerInfoVO.getId()), 6, 0, params.getMode());
        List<ScoreVO> scoreVOArray= OsuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreDTOS));
        playerInfoVO.setBps(scoreVOArray);
        ProfileTheme theme;
        String defaultBackground=ResourceMonitor.getResourcePath().toAbsolutePath()+ "/static/assets/whitespace_" +CommonTool.randomNumberGenerator(3) +".png";
        if (params.getProfileCustomizationPO()!=null) {
            CustomizeServiceImpl.validateProfileCustomizationCache(params.getProfileCustomizationPO());
            if(params.getProfileCustomizationPO().getVerified()>0){
                playerInfoVO.setProfileBackgroundUrl(ResourceMonitor.getResourcePath().toAbsolutePath()+ "/osuFiles/playerCustomization/profile/" + playerInfoVO.getId() +".jpg");
                if(params.getProfileCustomizationPO().getHue()!=null)
                    theme=ProfileTheme.getInstance(params.getProfileCustomizationPO().getPreferred_type(),params.getProfileCustomizationPO().getHue());
                else
                    theme=ProfileTheme.getInstance(params.getProfileCustomizationPO().getPreferred_type(),CommonTool.getDominantHueColorThief(new File(playerInfoVO.getProfileBackgroundUrl())));
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

        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createInfoPanel(playerInfoVO, theme));
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
        List<ScoreSequence> scoreSequences=TransformerUtil.scoreSequenceListTransform(scoreDTOS);
        OsuToolsUtil.setUpImageStaticSequence(scoreSequences);
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createScoreListDetailed(scoreSequences,info,params.getFrom()));
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
        else playerInfoDTO = dataExtractor.extractPlayerInfoDTO(params.getPlayerName(),params.getMode());
        return playerInfoDTO;
    }


}
