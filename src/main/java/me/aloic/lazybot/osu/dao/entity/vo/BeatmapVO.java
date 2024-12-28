package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeatmapVO
{
    private Double accuracy;
    private Double ar;
    private Integer beatmapset_id;
    private Double bpm;
    private Boolean convert;
    private Double cs;
    private Double drain;
    private Integer hit_length;
    private Integer total_length;
    private String achieved_time;
    private Integer mode_int;
    private Integer passcount;
    private String url;
    private Double difficult_rating;
    private String version;
    private String status;
    private String artist;
    private String title;
    private String coverUrl;
    private String creator;
    private Integer max_combo;
    private Integer sid;
    private Integer bid;
    private String bgUrl;
    private Double aim_star;
    private Double spd_star;
    private Double pp_aim;
    private Double pp_spd;
    private Double pp_acc;
    private BeatmapAttributeVO attributes;

}
