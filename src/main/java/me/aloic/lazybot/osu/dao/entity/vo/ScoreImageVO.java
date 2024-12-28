package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatistics;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreImageVO
{
    private String playerName;
    private Double accuracy;
    private String[] mods;
    private Long score;
    private Integer maxComboOfThisScore;
    private Double pp;
    private String achievedTime;
    private String mode;
    private BeatmapVO beatmap;
    private String avatarUrl;
    private ScoreStatistics statistics;
    private PerformanceVO ppDetailsLocal;
}
