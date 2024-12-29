package me.aloic.lazybot.osu.utils;

import jakarta.annotation.Nonnull;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapVO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.util.CommonTool;
import me.aloic.lazybot.util.DataObjectExtractor;
import me.aloic.lazybot.util.TransformerUtil;

import java.util.List;
import java.util.Objects;

public class OsuToolsUtil
{
    public static Integer getUserIdByUsername(@Nonnull String username, @Nonnull String accessToken)
    {
        PlayerInfoDTO playerInfoDTO = DataObjectExtractor.extractPlayerInfo(accessToken,username,"osu");
        return playerInfoDTO.getId();
    }
    public static Integer getUserIdByUsername(@Nonnull String username, @Nonnull UserTokenPO tokenPO)
    {
        Integer playerId= tokenPO.getPlayer_id();
        if(!Objects.equals(username, tokenPO.getPlayer_name()))
            playerId= OsuToolsUtil.getUserIdByUsername(username, tokenPO.getAccess_token());
        return playerId;
    }
    public static BeatmapVO setupBeatmapVO(BeatmapDTO beatmapDTO)
    {
        BeatmapVO beatmapVO = TransformerUtil.beatmapTransform(beatmapDTO);
        beatmapVO.setBgUrl(AssertDownloadUtil.svgAbsolutePath(beatmapVO.getBeatmapset_id()));
        return beatmapVO;
    }
    public static ScoreVO setupScoreVO(BeatmapDTO beatmapDTO, ScoreLazerDTO scoreLazerDTO)
    {
        ScoreVO scoreVO = TransformerUtil.transformScoreLazerToScoreVO(scoreLazerDTO);
        scoreVO.setBeatmap(OsuToolsUtil.setupBeatmapVO(beatmapDTO));
        try {
            scoreVO.setPpDetailsLocal(RosuUtil.getPPStats(AssertDownloadUtil.beatmapPath(scoreVO), scoreVO));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error during recalculations/重算成绩详情时出错: " + e.getMessage());
        }
        if (CommonTool.modsContainsAnyOfStarChanging(scoreVO.getMods()))
            scoreVO.getBeatmap().setDifficult_rating(scoreVO.getPpDetailsLocal().getStar());
        if (scoreVO.getPp() == null)
            scoreVO.setPp(scoreVO.getPpDetailsLocal().getCurrentPP());
        ModCalculatorUtil.afterModMapInfo(scoreVO);
        return scoreVO;
    }

    public static List<ScoreVO> setUpImageStatic(List<ScoreVO> scoreVOList)
    {
        for(ScoreVO scoreVO:scoreVOList) {
            scoreVO.getBeatmap().setBgUrl(AssertDownloadUtil.svgAbsolutePath(scoreVO.getBeatmap().getBeatmapset_id()));
            try{
                scoreVO.setPpDetailsLocal(RosuUtil.getPPStats(AssertDownloadUtil.beatmapPath(scoreVO),scoreVO));
                if(scoreVO.getPpDetailsLocal().getStar()!=null)
                    scoreVO.getBeatmap().setDifficult_rating(scoreVO.getPpDetailsLocal().getStar());
            }
            catch (Exception e) {
                throw new RuntimeException("重算成绩详情时出错: " + e.getMessage());
            }
        }
        return scoreVOList;
    }
    public static PlayerInfoVO setupPlayerInfoVO(PlayerInfoDTO playerInfoDTO)
    {
        playerInfoDTO.setAvatar_url((AssertDownloadUtil.avatarAbsolutePath(playerInfoDTO,false)));
        return TransformerUtil.userTransform(playerInfoDTO);
    }
}
