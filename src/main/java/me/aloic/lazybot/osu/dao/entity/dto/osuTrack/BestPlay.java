package me.aloic.lazybot.osu.dao.entity.dto.osuTrack;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class BestPlay implements Serializable
{
    private Integer user;
    private Integer beatmap_id;
    private Integer score;
    private Double pp;
    private Integer mods;
    private String rank;
    private String score_time;
    private String update_time;
}
