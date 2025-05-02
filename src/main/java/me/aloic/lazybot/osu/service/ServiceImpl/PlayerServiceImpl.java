package me.aloic.lazybot.osu.service.ServiceImpl;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.vo.NoChokeListVO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.theme.preset.ProfileLightTheme;
import me.aloic.lazybot.osu.theme.preset.ProfileTheme;
import me.aloic.lazybot.osu.utils.*;
import me.aloic.lazybot.parameter.*;
import me.aloic.lazybot.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService
{
    private static final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);
    @Override
    public byte[] score(ScoreParameter params) throws Exception
    {
        BeatmapUserScoreLazer beatmapUserScoreLazer = DataObjectExtractor.extractBeatmapUserScore(params.getAccessToken(),
                String.valueOf(params.getBeatmapId()), params.getPlayerId(), params.getMode(), params.getModCombination());

            ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(
                    DataObjectExtractor.extractBeatmap(params.getAccessToken(), String.valueOf(params.getBeatmapId()), params.getMode()),
                    beatmapUserScoreLazer.getScore(),
                    false);
            verifyBeatmapsCache(scoreVO);
            return SVGRenderUtil.renderScoreToByteArray(scoreVO, params.getVersion(), getDominantColorArray(scoreVO));
    }

    @Override
    public byte[] recent(RecentParameter params, Integer type) throws IOException
    {
        List<ScoreLazerDTO> scoreList = DataObjectExtractor.extractRecentScoreList(params.getAccessToken(), params.getPlayerId(), type, params.getIndex(), params.getMode());
        if(params.getIndex()>scoreList.size()) {
            throw new LazybotRuntimeException("超出能索引的最大距离，当前为: "+params.getIndex()+", 最大为: " + scoreList.size());
        }
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(
                DataObjectExtractor.extractBeatmap(params.getAccessToken(), String.valueOf(scoreList.get(params.getIndex() - 1).getBeatmap_id()), params.getMode()),
                scoreList.get(params.getIndex() - 1),
                false);
        verifyBeatmapsCache(scoreVO);
        return SVGRenderUtil.renderScoreToByteArray(scoreVO, params.getVersion(), getDominantColorArray(scoreVO));

    }
    @Override
    public byte[] bp(BpParameter params) throws IOException
    {
        List<ScoreLazerDTO> scoreDTO = DataObjectExtractor.extractUserBestScoreList(
                params.getAccessToken(),
                String.valueOf(params.getPlayerId()),
                params.getIndex()-1,
                params.getMode());

        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(
                DataObjectExtractor.extractBeatmap(params.getAccessToken(),String.valueOf(scoreDTO.getFirst().getBeatmap_id()),params.getMode()),
                scoreDTO.getFirst(),
                false);
        verifyBeatmapsCache(scoreVO);
        return SVGRenderUtil.renderScoreToByteArray(scoreVO,params.getVersion(), getDominantColorArray(scoreVO));
    }
    @Override
    public byte[] bplistCardView(BplistParameter params) throws Exception
    {
        PlayerInfoDTO playerInfoDTO = DataObjectExtractor.extractPlayerInfo(params.getAccessToken(),params.getPlayerName(),params.getMode());
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        List<ScoreLazerDTO> scoreDTOS=DataObjectExtractor.extractUserBestScoreList(
                params.getAccessToken(),
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
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(params.getInfoDTO());
        List<ScoreLazerDTO> scoreDTOList=DataObjectExtractor.extractUserBestScoreList(
                params.getAccessToken(),
                String.valueOf(params.getInfoDTO().getId()),
                100,0,params.getMode());
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
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createBpCard(info,scoreVOList,0,4,
                "Current command: /todayBp. Showing new Bps within " + params.getMaxDays() +" day(s)"));
    }
    @Override
    public byte[] bpvs(BpvsParameter params)throws Exception
    {
        CompletableFuture<PlayerInfoDTO> playerInfoFuture = CompletableFuture.supplyAsync(() -> {
            try {
                PlayerInfoDTO dto = DataObjectExtractor.extractPlayerInfo(params.getAccessToken(), params.getPlayerName(), params.getMode());
                dto.setAvatar_url(AssertDownloadUtil.avatarAbsolutePath(dto, false));
                return dto;
            } catch (Exception e) {
                throw new LazybotRuntimeException("[bpvs指令] 异步获取玩家" + params.getPlayerName() + "数据失败"+ e.getMessage());
            }
        });

        CompletableFuture<PlayerInfoDTO> comparePlayerInfoFuture = CompletableFuture.supplyAsync(() -> {
            try {
                PlayerInfoDTO dto = DataObjectExtractor.extractPlayerInfo(params.getAccessToken(), params.getComparePlayerName(), params.getMode());
                dto.setAvatar_url(AssertDownloadUtil.avatarAbsolutePath(dto, false));
                return dto;
            } catch (Exception e) {
                throw new LazybotRuntimeException("[bpvs指令] 异步获取玩家" + params.getComparePlayerName() + "数据失败"+ e.getMessage());
            }
        });

        CompletableFuture<byte[]> resultFuture = playerInfoFuture.thenCombineAsync(comparePlayerInfoFuture, (playerInfoDTO, comparePlayerInfoDTO) -> {
            try {
                if (Objects.equals(playerInfoDTO.getId(), comparePlayerInfoDTO.getId())) throw new LazybotRuntimeException("你不能和自己对比");
                CompletableFuture<List<ScoreLazerDTO>> scoreFuture = CompletableFuture.supplyAsync(() ->
                        DataObjectExtractor.extractUserBestScoreList(params.getAccessToken(), String.valueOf(playerInfoDTO.getId()), 100, 0, params.getMode()));

                CompletableFuture<List<ScoreLazerDTO>> compareScoreFuture = CompletableFuture.supplyAsync(() ->
                        DataObjectExtractor.extractUserBestScoreList(params.getAccessToken(), String.valueOf(comparePlayerInfoDTO.getId()), 100, 0, params.getMode()));

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
        try {
            return resultFuture.get();
        }
        catch (CompletionException e) {
            Throwable rootCause = e.getCause();
            if (rootCause instanceof LazybotRuntimeException) {
                throw e;
            }
        }
        return resultFuture.get();
    }
    @Override
    public byte[] noChoke(GeneralParameter params, int type) throws Exception
    {
        List<ScoreLazerDTO> originalScoreArray=DataObjectExtractor.extractUserBestScoreList(
                params.getAccessToken(),
                String.valueOf(params.getInfoDTO().getId()),
                100,0,params.getMode());

        NoChokeListVO noChokeListVO=OsuToolsUtil.setupNoChokeList(
                OsuToolsUtil.setupPlayerInfoVO(params.getInfoDTO()),
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
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(DataObjectExtractor.extractPlayerInfo(params.getAccessToken(),params.getPlayerName(),params.getMode()));
        playerInfoVO.setMode(params.getMode());
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createInfoCard(playerInfoVO));
    }

    @Override
    public byte[] profile(ProfileParameter params) throws Exception {
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(params.getInfoDTO());
        playerInfoVO.setMode(params.getMode());
        List<ScoreLazerDTO> scoreDTOS=DataObjectExtractor.extractUserBestScoreList(params.getAccessToken(), String.valueOf(playerInfoVO.getId()), 6, 0, params.getMode());
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
            ApiRequestStarter playerRequest = new ApiRequestStarter(URLBuildUtil.buildURLOfPlayerInfo(name),params.getAccessToken());
            PlayerInfoDTO playerInfoDTO = playerRequest.executeRequest(ContentUtil.HTTP_REQUEST_TYPE_GET, PlayerInfoDTO.class);
            if(playerInfoDTO.getId()==null){
                builder.append(name).append(" --> ")
                        .append("没这B人\n");
            }
            else {
                builder.append(playerInfoDTO.getUsername()).append(" --> ").append(playerInfoDTO.getId()).append("\n");
            }
        }

        return builder.toString();
    }
    @Override
    public byte[] bplistListView(BplistParameter params) throws Exception
    {
        PlayerInfoDTO playerInfoDTO = DataObjectExtractor.extractPlayerInfo(params.getAccessToken(),params.getPlayerName(),params.getMode());
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        List<ScoreLazerDTO> scoreDTOS=DataObjectExtractor.extractUserBestScoreList(
                params.getAccessToken(),
                String.valueOf(playerInfoDTO.getId()),
                params.getTo()-params.getFrom()+1,
                params.getFrom()-1,
                params.getMode());
        List<ScoreSequence> scoreSequences=TransformerUtil.scoreSequenceListTransform(scoreDTOS);
        OsuToolsUtil.setUpImageStaticSequence(scoreSequences);
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createScoreListDetailed(scoreSequences,info,params.getFrom()));
    }



    private boolean verifyBeatmapsCache(ScoreVO scoreVO) {
        String checksum=CommonTool.calculateMD5(new File(AssertDownloadUtil.beatmapPath(scoreVO,false).toUri()));
        if (!checksum.equals(scoreVO.getBeatmap().getChecksum())) {
            logger.warn("Checksum mismatch, downloading beatmap: {} != {}", scoreVO.getBeatmap().getChecksum(), checksum);
            AssertDownloadUtil.beatmapPath(scoreVO, true);
            return false;
        }
        return true;
    }

    private int[] getDominantColorArray(ScoreVO scoreVO) throws IOException {
        return CommonTool.getDominantColorColorThief(new File(scoreVO.getBeatmap().getBgUrl()));
    }


}
