package me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModSetting implements Serializable
{
    private Double speed_change;
    private Double drain_rate;
    private Double circle_size;
    private Double approach_rate;
    private Boolean extended_limits;
    private Double overall_Difficulty;
    private Long seed;
}
