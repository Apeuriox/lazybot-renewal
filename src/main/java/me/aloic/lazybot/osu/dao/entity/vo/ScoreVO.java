package me.aloic.lazybot.osu.dao.entity.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreVO implements Comparable<ScoreVO>
{
    private String user_name;
    private Double accuracy;
    private String[] mods;
    private List<Mod> modJSON;
    private Long score;
    private Integer maxCombo;
    private ScoreStatisticsLazer statistics;
    private Integer positionInList;
    private Double pp;
    private String rank;
    private String create_at;
    private String mode;
    private BeatmapVO beatmap;
    private String avatarUrl;
    private PerformanceVO ppDetailsLocal;
    private Boolean isLazer;
    private Boolean isPerfectCombo;

    @Override
    public int compareTo(@NotNull ScoreVO o)
    {
        if (this.pp > o.pp) {
            return 1;
        } else if (this.pp < o.pp) {
            return -1;
        }
        return 0;
    }


    public void setStatistics(ScoreStatisticsLazer statistics)
    {
        this.statistics = statistics;
        this.statistics.reInitialize();
    }
}
