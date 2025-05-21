package me.aloic.lazybot.osu.dao.entity.dto.beatmap;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.MaximumStatistics;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.player.CurrentUserAttributes;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class ScoreLazerDTO implements Serializable
{
    private Long classic_total_score;
    private Boolean preserve;
    private Boolean processed;
    private Boolean ranked;
    private MaximumStatistics maximum_statistics;
    private List<Mod> mods;
    private ScoreStatisticsLazer statistics;
    private Integer total_score_without_mods;
    private Integer beatmap_id;
    private Long best_id;
    private Long id;
    private String rank;
    private String type;
    private Integer user_id;
    private Double accuracy;
    private Integer build_id;
    private String ended_at;
    private Boolean has_replay;
    private Boolean is_perfect_combo;
    private Boolean legacy_perfect;
    private Long legacy_score_id;
    private Long legacy_total_score;
    private Integer max_combo;
    private Boolean passed;
    private Double pp;
    private Integer ruleset_id;
    private String started_at;
    private Integer total_score;
    private Boolean replay;
    private CurrentUserAttributes current_user_attributes;
    private BeatmapDTO beatmap;
    private PlayerInfoDTO user;
    private BeatmapsetDTO beatmapset;


    public String[] getModsArray()
    {
        return mods.stream().map(Mod::getAcronym).toArray(String[]::new);
    }




}
