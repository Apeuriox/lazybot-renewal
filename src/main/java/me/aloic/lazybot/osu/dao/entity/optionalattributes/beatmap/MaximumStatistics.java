package me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
@Data
@AllArgsConstructor
public class MaximumStatistics implements Serializable
{
    private Integer great;
    private Integer ignore_hit;
    private Integer large_bonus;
    private Integer small_bonus;
    private Integer large_tick_hit;
    private Integer slider_tail_hit;
    private Integer legacy_combo_increase;

}
