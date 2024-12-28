package me.aloic.lazybot.osu.dao.entity.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class PlayerInfoPO implements Serializable {
    private String username;
    private String mode;
    private Double pp;
    private Integer global_rank;
    private Integer country_rank;
    private String country;
    private Long rank_total_score;
    private Double accuracy;
    private Integer play_count;
    private Long total_hit_count;
    private Long total_play_time;

}
