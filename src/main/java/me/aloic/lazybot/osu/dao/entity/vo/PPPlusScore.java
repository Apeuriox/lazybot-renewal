package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class PPPlusScore extends ScoreVO
{
    private PPPlusPerformance plusPerformance;
    private PPPlusPerformance maxPerformance;

    public PPPlusScore(ScoreVO scoreVO)
    {
        this.setUser_name(scoreVO.getUser_name());
        this.setAccuracy(scoreVO.getAccuracy());
        this.setModJSON(scoreVO.getModJSON());
        this.setScore(scoreVO.getScore());
        this.setMaxCombo(scoreVO.getMaxCombo());
        this.setStatistics(scoreVO.getStatistics());
        this.setPositionInList(scoreVO.getPositionInList());
        this.setPp(scoreVO.getPp());
        this.setRank(scoreVO.getRank());
        this.setCreate_at(scoreVO.getCreate_at());
        this.setMode(scoreVO.getMode());
        this.setBeatmap(scoreVO.getBeatmap());
        this.setAvatarUrl(scoreVO.getAvatarUrl());
        this.setPpDetailsLocal(scoreVO.getPpDetailsLocal());
        this.setIsLazer(scoreVO.getIsLazer());
        this.setIsPerfectCombo(scoreVO.getIsPerfectCombo());


    }
}
