package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreSequence
{
    private String playerName;
    private Double accuracy;
    private List<Mod> modList;
    private Long score;
    private Integer maxCombo;
    private ScoreStatisticsLazer statistics;
    private Integer positionInList;
    private String rank;
    private String achievedTime;
    private Integer rulesetId;
    private BeatmapVO beatmap;
    private String avatarUrl;
    private PerformanceVO ppDetails;
    private Boolean isLazer;
    private Boolean isPerfectCombo;
    private Boolean isFC;
    private Integer differenceBetweenNextScore;
    private Double pp;
}
