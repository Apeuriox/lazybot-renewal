package me.aloic.lazybot.osu.dao.entity.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class PlayerProfilePO implements Serializable
{
    private String playerName;
    private String currentMode;
    private Double totalPp;
    private Integer globalRank;
    private Integer countryRank;
    private String country;
    private Long rankTotalScore;
    private Double profileAccuracy;
    private Integer playCount;
    private Long totalTit;
    private Long totalPlayTime;
    private List<ScoreVO> bestPerformances;

}

