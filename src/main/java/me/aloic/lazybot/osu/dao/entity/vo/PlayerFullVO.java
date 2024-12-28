package me.aloic.lazybot.osu.dao.entity.vo;

import java.util.List;

public class PlayerFullVO
{
    private String playerName;
    private String mode;
    private Double totalPP;
    private Integer globalRank;
    private String country;
    private Integer countryRank;
    private Long rankTotalScore;
    private Double accuracy;
    private Integer playCount;
    private Long totalHitCount;
    private Long totalPlayTime;
    private String avatarUrl;
    private List<ScoreVO> bestScores;
}
