package me.aloic.lazybot.osu.dao.entity.dto.osuTrack;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@NoArgsConstructor
public class UserDifference implements Serializable
{
    private String username;
    private Integer mode;
    private Integer playcount;
    private Integer pp_rank;
    private Integer pp_raw;
    private Integer accuracy;
    private Long total_score;
    private Long ranked_score;
    private Integer count300;
    private Integer count50;
    private Integer count100;
    private Integer level;
    private Integer count_rank_a;
    private Integer count_rank_s;
    private Integer count_rank_ss;
    private Boolean levelup;
    private Boolean first;
    private Boolean exists;
    private List<HitScoreFull> newhs;

}
