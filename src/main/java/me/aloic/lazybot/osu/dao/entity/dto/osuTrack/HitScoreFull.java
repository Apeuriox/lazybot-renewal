package me.aloic.lazybot.osu.dao.entity.dto.osuTrack;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class HitScoreFull implements Serializable
{
    private String beatmap_id;
    private String score_id;
    private String score;
    private String maxcombo;
    private String count50;
    private String count100;
    private String count300;
    private String countmiss;
    private String countkatu;
    private String countgeki;
    private String perfect;
    private String enabled_mods;
    private String user_id;
    private String date;
    private String rank;
    private String pp;
    private String replay_available;
    private Integer ranking;

}
