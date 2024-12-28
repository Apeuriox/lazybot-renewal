package me.aloic.lazybot.osu.dao.entity.dto.beatmap;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatistics;


import java.io.Serializable;


@Data
@NoArgsConstructor
public class ScoreDTO implements Serializable {
    private Long id;
    private Long best_id;
    private Integer user_id;
    private Double accuracy;
    private String[] mods;
    private Long score;
    private Integer max_combo;
    private Boolean perfect;
    private ScoreStatistics statistics;
    private Boolean passed;
    private Double pp;
    private String rank;
    private String created_at;
    private String mode;
    private Integer mode_int;
    private Boolean replay;
    private PlayerInfoDTO user;
    private BeatmapDTO beatmap;
    private BeatmapsetDTO beatmapset;

//    private Double accuracy;
//    private Integer beatmap_id;
//    private Integer best_id;
//    private Integer build_id;
//    private Timestamp ended_at;
//    private Boolean has_replay;
//    private Long id;
//    private Boolean is_perfect_combo;
//    private Boolean legacy_perfect;
//    private Long legacy_score_id;
//    private Long legacy_total_score;
//    private ScoreStatistics maximum_statistics;
//    private String[] mods;
//    private Boolean passed;
//    private Integer playlist_item_id;
//    private Double pp;
//    private Boolean preserve;
//    private String rank;
//    private Boolean ranked;
//    private Integer room_id;
//    private Integer ruleset_id;
//    private Timestamp started_at;
//    private ScoreStatistics statistics;
//    private Long total_score;
//    private String type;
//    private Integer user_id;
}
