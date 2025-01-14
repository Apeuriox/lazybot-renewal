package me.aloic.lazybot.osu.service.ServiceImpl;

import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.vo.NoChokeListVO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.*;
import me.aloic.lazybot.parameter.*;
import me.aloic.lazybot.util.*;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService
{

    @Override
    public byte[] score(ScoreParameter params) throws Exception {
        BeatmapUserScoreLazer beatmapUserScoreLazer = DataObjectExtractor.extractBeatmapUserScore(params.getAccessToken().getAccess_token(),
                String.valueOf(params.getBeatmapId()),params.getPlayerInfo().getId(),params.getMode(),params.getModCombination());
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(DataObjectExtractor.extractBeatmap(params.getAccessToken().getAccess_token(), String.valueOf(params.getBeatmapId()),params.getMode())
                ,beatmapUserScoreLazer.getScore());
        return SVGRenderUtil.renderScoreToByteArray(scoreVO,params.getVersion(), Optional.ofNullable(params.getPlayerInfo().getProfile_hue()).orElse(333));
    }

    @Override
    public byte[] recent(RecentParameter params, Integer type){
        List<ScoreLazerDTO> scoreList = DataObjectExtractor.extractRecentScoreList(params.getAccessToken().getAccess_token(), params.getPlayerInfo().getId(), type, params.getMode());
        if(params.getIndex()-1>=scoreList.size()) {
            throw new RuntimeException("超出能索引的最大距离，当前为: "+params.getIndex()+", 最大为: " +scoreList.size());
        }
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(DataObjectExtractor.extractBeatmap(
                params.getAccessToken().getAccess_token(),
                String.valueOf(scoreList.get(params.getIndex()).getBeatmap_id()),params.getMode()),
                scoreList.get(params.getIndex()-1));
        return SVGRenderUtil.renderScoreToByteArray(scoreVO,params.getVersion(),Optional.ofNullable(params.getPlayerInfo().getProfile_hue()).orElse(333));
    }
    @Override
    public byte[] bp(BpParameter params)
    {
        List<ScoreLazerDTO> scoreDTO = DataObjectExtractor.extractUserBestScoreList(params.getAccessToken().getAccess_token(), String.valueOf(params.getPlayerInfo().getId()),params.getIndex()-1,params.getMode());
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(DataObjectExtractor.extractBeatmap(
                        params.getAccessToken().getAccess_token(),
                        String.valueOf(scoreDTO.getFirst().getBeatmap_id()),params.getMode()),
                scoreDTO.getFirst());
        return SVGRenderUtil.renderScoreToByteArray(scoreVO,params.getVersion(),Optional.ofNullable(params.getPlayerInfo().getProfile_hue()).orElse(333));
    }
    @Override
    public byte[] bplistCardView(BplistParameter params) throws Exception
    {
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(params.getPlayerInfo());
        List<ScoreLazerDTO> scoreDTOS=DataObjectExtractor.extractUserBestScoreList(
                params.getAccessToken().getAccess_token(),
                String.valueOf(params.getPlayerInfo().getId()),
                params.getTo()-params.getFrom()+1,
                params.getFrom(),
                params.getMode());
        List<ScoreVO> scoreVOArray= OsuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreDTOS));
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createBpCard(info,scoreVOArray,params.getFrom(),1));
    }
    @Override
    public byte[] todayBp(TodaybpParameter params) throws Exception
    {
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(params.getPlayerInfo());
        List<ScoreLazerDTO> scoreDTOList=DataObjectExtractor.extractUserBestScoreList(params.getAccessToken().getAccess_token(), String.valueOf(params.getPlayerInfo().getId()),100,0,params.getMode());
        //Why not directly filter scoreDTOs? cuz we need this procedure to wire Indexes
        List<ScoreVO> scoreVOList=TransformerUtil.scoreTransformForList(scoreDTOList);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC+0"));
        scoreVOList = scoreVOList.stream()
                .filter(score -> {
                    ZonedDateTime scoreTime = ZonedDateTime.parse(score.getCreate_at(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    return scoreTime.isAfter(now.minusDays(params.getMaxDays()));
                })
                .collect(Collectors.toList());
        if(scoreVOList.isEmpty()) {
            throw new RuntimeException("没有找到符合条件的bp");
        }
        OsuToolsUtil.setUpImageStatic(scoreVOList);
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createBpCard(info,scoreVOList,0,4,
                "Current command: /todayBp. Showing new Bps within " + params.getMaxDays() +" day(s)"));
    }
    @Override
    public byte[] bpvs(BpvsParameter params)throws Exception
    {
        params.getPlayerInfo().setAvatar_url((AssertDownloadUtil.avatarAbsolutePath(params.getPlayerInfo(),false)));
        PlayerInfoDTO comparePlayerInfoDTO = DataObjectExtractor.extractPlayerInfo(params.getAccessToken().getAccess_token(),params.getComparePlayerName(),params.getMode());
        comparePlayerInfoDTO.setAvatar_url((AssertDownloadUtil.avatarAbsolutePath(comparePlayerInfoDTO,false)));
        List<ScoreLazerDTO> scoreDTOS=DataObjectExtractor.extractUserBestScoreList(params.getAccessToken().getAccess_token(), String.valueOf(params.getPlayerInfo().getId()),100,0,params.getMode());
        List<ScoreLazerDTO> compareScoreDTOS=DataObjectExtractor.extractUserBestScoreList(params.getAccessToken().getAccess_token(), String.valueOf(comparePlayerInfoDTO.getId()),100,0,params.getMode());
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createCompareBpList(params.getPlayerInfo(),comparePlayerInfoDTO,TransformerUtil.scoreTransformForArray(scoreDTOS),TransformerUtil.scoreTransformForArray(compareScoreDTOS)));
    }
    @Override
    public byte[] noChoke(GeneralParameter params, int type) throws Exception
    {
        List<ScoreLazerDTO> originalScoreArray=DataObjectExtractor.extractUserBestScoreList(params.getAccessToken().getAccess_token(), String.valueOf(params.getPlayerInfo().getId()),100,0,params.getMode());
        NoChokeListVO noChokeListVO=OsuToolsUtil.setupNoChokeList(OsuToolsUtil.setupPlayerInfoVO(params.getPlayerInfo()),TransformerUtil.scoreTransformForList(originalScoreArray),type);
        if(type==1) {
            return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createBpCard(noChokeListVO.getInfo(),noChokeListVO.getScoreList(),0,2));
        }
        else {
            return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createBpCard(noChokeListVO.getInfo(),noChokeListVO.getScoreList(),0,4,
                    "All scores are recalculated with FC. Plz keep in mind that this may not reflect your skill correctly."));
        }
    }
    @Override
    public byte[] card(GeneralParameter params) throws Exception {
        PlayerInfoVO playerInfoVO = OsuToolsUtil.setupPlayerInfoVO(DataObjectExtractor.extractPlayerInfo(params.getAccessToken().getAccess_token(),params.getPlayerName(),params.getMode()));
        playerInfoVO.setMode(params.getMode());
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createInfoCard(playerInfoVO));
    }
    @Override
    public String nameToId(NameToIdParameter params) {
        StringBuilder builder = new StringBuilder();
        for(String name:params.getTargets()){
            ApiRequestStarter playerRequest = new ApiRequestStarter(URLBuildUtil.buildURLOfPlayerInfo(name),params.getAccessToken().getAccess_token());
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
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(params.getPlayerInfo());
        List<ScoreLazerDTO> scoreDTOS=DataObjectExtractor.extractUserBestScoreList(
                params.getAccessToken().getAccess_token(),
                String.valueOf(params.getPlayerInfo().getId()),
                params.getTo()-params.getFrom()+1,
                params.getFrom(),
                params.getMode());
        List<ScoreSequence> scoreSequences=TransformerUtil.scoreSequenceListTransform(scoreDTOS);
        OsuToolsUtil.setUpImageStaticSequence(scoreSequences);
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createScoreListDetailed(scoreSequences,info));
    }

}
