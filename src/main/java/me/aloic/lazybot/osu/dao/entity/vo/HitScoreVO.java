package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitScoreVO
{
    private Integer beatmap_id;
    private Integer score;
    private Double pp;
    private Integer mods;
    private String rank;
    private Date achievedTime;
    private String scoreTimeJSON;
}
