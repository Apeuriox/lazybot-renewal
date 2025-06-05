package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.enums.OsuMode;
import org.spring.osu.extended.rosu.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeatmapPerformance extends BeatmapVO
{
    private JniDifficultyAttributes difficultyAttributes;
    private double lengthBonus;
    private Integer playCount;
    private Integer favouriteCount;
    private Integer countSpinners;
    private Integer countCircles;
    private Integer countSliders;
    private OsuMode mode;
}
