package me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ScoreStatistics implements Serializable {
    private Integer count_50;
    private Integer count_100;
    private Integer count_300;
    private Integer count_geki;
    private Integer count_katu;
    private Integer count_miss;

}
