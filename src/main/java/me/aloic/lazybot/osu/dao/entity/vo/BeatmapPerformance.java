package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.spring.osu.extended.rosu.JniPerformanceAttributes;
import org.spring.osu.extended.rosu.OsuDifficultyAttributes;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeatmapPerformance extends BeatmapVO
{
    private OsuDifficultyAttributes performanceAttributes;
    private Integer playCount;
    private Integer favouriteCount;
}
