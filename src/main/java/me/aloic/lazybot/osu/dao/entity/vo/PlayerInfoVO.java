package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfoVO
{
    private String playerName;
    private String mode;
    private double performancePoint;
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

}
