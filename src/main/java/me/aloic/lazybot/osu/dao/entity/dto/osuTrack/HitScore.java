package me.aloic.lazybot.osu.dao.entity.dto.osuTrack;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class HitScore implements Serializable
{
    private Integer beatmap_id;
    private Integer score;
    private Double pp;
    private Integer mods;
    private String rank;
    private String score_time;
    private String update_time;
}
