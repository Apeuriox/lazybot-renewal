package me.aloic.lazybot.osu.dao.entity.optionalattributes.player;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class StatisticsRuleSet implements Serializable {
    private Long count_100;
    private Long count_300;
    private Long count_50;
    private Long count_miss;
    private Level level;
    private Integer global_rank;
    private Object global_rank_exp;
    private Double pp;
    private Double pp_exp;
    private Long ranked_score;
    private Double hit_accuracy;
    private Integer play_count;
    private Long play_time;
    private Long total_score;
    private Long total_hits;
    private Integer maximum_combo;
    private Integer replays_watched_by_others;
    private Boolean is_ranked;
    private GradeCounts grade_counts;
}
