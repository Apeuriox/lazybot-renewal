package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfoVO
{
    private String playerName;
    private String mode;
    private Double performancePoint;
    private Integer globalRank;
    private String country;
    private Integer countryRank;
    private Long rankTotalScore;
    private Double accuracy;
    private Integer playCount;
    private Long totalHitCount;
    private Long totalPlayTime;
    private String avatarUrl;
    private String fixedPPString;
    private Integer primaryColor;

    private Integer level;
    private Integer levelProgress;
    private String countryCode;
    private Long totalScore;
    private List<Integer> rankHistory;
    private List<ScoreVO> bps;
    private Integer id;


}
