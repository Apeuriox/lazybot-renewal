package me.aloic.lazybot.osu.service.ServiceImpl;

import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.service.AnalysisService;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.osu.utils.SVGRenderUtil;
import me.aloic.lazybot.osu.utils.SvgUtil;
import me.aloic.lazybot.parameter.BpifParameter;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.util.CommonTool;
import me.aloic.lazybot.util.DataObjectExtractor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class AnalysisServiceImpl implements AnalysisService
{
    @Override
    public byte[] bpIf(BpifParameter params) throws IOException
    {
        PlayerInfoDTO playerInfoDTO= DataObjectExtractor.extractPlayerInfo(params.getAccessToken().getAccess_token(),params.getPlayerName(),params.getMode());
        PlayerInfoVO info= OsuToolsUtil.setupPlayerInfoVO(playerInfoDTO);
        List<ScoreLazerDTO> originalScoreArray=DataObjectExtractor.extractUserBestScoreList(params.getAccessToken().getAccess_token(), String.valueOf(playerInfoDTO.getId()),100,0,params.getMode());
        List<ScoreVO> scoreList=OsuToolsUtil.setupBpifScoreList(params,originalScoreArray,info);
        return SVGRenderUtil.renderSVGDocumentToByteArray(SvgUtil.createBpCard(info,scoreList.stream().limit(30).collect(Collectors.toList()),0,3,"/BpIf: Recalculate your Bps with desired mods. +mod to insert, -mod to remove, +mod! to replace."));
    }
    @Override
    public String recommendedDifficulty(GeneralParameter params) throws Exception
    {
        PlayerInfoDTO playerInfoDTO = DataObjectExtractor.extractPlayerInfo(params.getAccessToken().getAccess_token(), params.getPlayerName(), params.getMode());
        double recommended = Math.pow(playerInfoDTO.getStatistics().getPp(), 0.4) * 0.195;
        double recommendedFix=Math.pow((playerInfoDTO.getStatistics().getPp() * 0.85 / 20.0),0.33333);
        return "ppy说"+playerInfoDTO.getUsername() +"应该打" + CommonTool.toString(recommended) + "星的图\n"
                    +"另一种可能说" + playerInfoDTO.getUsername()+ "应该打"+CommonTool.toString(recommendedFix) + "星的图";

    }

}
