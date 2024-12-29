package me.aloic.lazybot.osu.service.ServiceImpl;

import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.BeatmapUserScoreLazer;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.osu.utils.SVGRenderUtil;
import me.aloic.lazybot.osu.utils.SvgUtil;
import me.aloic.lazybot.parameter.BpCommandParameter;
import me.aloic.lazybot.parameter.BplistCommandParameter;
import me.aloic.lazybot.parameter.RecentCommandParameter;
import me.aloic.lazybot.parameter.ScoreCommandParameter;
import me.aloic.lazybot.util.DataObjectExtractor;
import me.aloic.lazybot.util.TransformerUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService
{

    @Override
    public byte[] score(ScoreCommandParameter params) throws Exception {
        BeatmapUserScoreLazer beatmapUserScoreLazer = DataObjectExtractor.extractBeatmapUserScore(params.getAccessToken().getAccess_token(),
                params.getBeatmapId(),params.getPlayerId(),params.getMode(),params.getModCombination());
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(DataObjectExtractor.extractBeatmap(params.getAccessToken().getAccess_token(),params.getBeatmapId(),params.getMode())
                ,beatmapUserScoreLazer.getScore());
        return SVGRenderUtil.renderScoreToByteArray(scoreVO,params.getVersion());
    }

    @Override
    public byte[] recent(RecentCommandParameter params, Integer type){
        List<ScoreLazerDTO> scoreList = DataObjectExtractor.extractRecentScoreList(params.getAccessToken().getAccess_token(), params.getPlayerId(), type, params.getMode());
        if(params.getIndex()>=scoreList.size()) {
            throw new RuntimeException("超出能索引的最大距离，当前为: "+params.getIndex()+", 最大为: " +(scoreList.size()-1));
        }
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(DataObjectExtractor.extractBeatmap(
                params.getAccessToken().getAccess_token(),
                String.valueOf(scoreList.get(params.getIndex()).getBeatmap_id()),params.getMode()),
                scoreList.get(params.getIndex()));
        return SVGRenderUtil.renderScoreToByteArray(scoreVO,params.getVersion());
    }
    @Override
    public byte[] bp(BpCommandParameter params)
    {
        List<ScoreLazerDTO> scoreDTO = DataObjectExtractor.extractUserBestScoreList(params.getAccessToken().getAccess_token(), String.valueOf(params.getPlayerId()),params.getIndex(),params.getMode());
        if(params.getIndex()>100) {
            throw new IllegalArgumentException("INDEX must be less than 100");
        }
        ScoreVO scoreVO = OsuToolsUtil.setupScoreVO(DataObjectExtractor.extractBeatmap(
                        params.getAccessToken().getAccess_token(),
                        String.valueOf(scoreDTO.getFirst().getBeatmap_id()),params.getMode()),
                scoreDTO.getFirst());
        return SVGRenderUtil.renderScoreToByteArray(scoreVO,params.getVersion());
    }
    @Override
    public byte[] bplist(BplistCommandParameter params) throws Exception
    {
        PlayerInfoDTO playerInfoDTO = DataObjectExtractor.extractPlayerInfo(params.getAccessToken().getAccess_token(),params.getPlayerName(),params.getMode());
        PlayerInfoVO info = OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        if(params.getFrom()>100||params.getTo()>100) {
            throw new IllegalArgumentException("FROM and TO must be less than 100");
        }
        List<ScoreLazerDTO> scoreDTOS=DataObjectExtractor.extractUserBestScoreList(
                params.getAccessToken().getAccess_token(),
                String.valueOf(playerInfoDTO.getId()),
                params.getTo()-params.getFrom()+1,
                params.getFrom(),
                params.getMode());
        List<ScoreVO> scoreVOArray= OsuToolsUtil.setUpImageStatic(TransformerUtil.scoreTransformForList(scoreDTOS));
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createBpCard(info,scoreVOArray,params.getFrom(),params.getVersion()));
    }

}
