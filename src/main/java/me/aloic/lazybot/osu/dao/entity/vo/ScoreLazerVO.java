package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreLazerVO
{
    private String user_name;
    private Double accuracy;
    private String[] mods;
    //scaled score: classic_total_score
    private Long score;
    private Integer maxCombo;
    private ScoreStatisticsLazer statistics;
    private Boolean passed;
    private Double pp;
    private String rank;
    private String create_at;
    private String mode;
    private BeatmapVO beatmap;
    private String avatarUrl;
    private PerformanceVO ppDetailsLocal;

}
