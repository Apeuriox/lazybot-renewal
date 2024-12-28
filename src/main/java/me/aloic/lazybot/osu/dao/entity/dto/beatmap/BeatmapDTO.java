package me.aloic.lazybot.osu.dao.entity.dto.beatmap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BeatmapDTO extends BeatmapCompactDTO
{
    private Double accuracy;
    private Double ar;
    private Double bpm;
    private Boolean convert;
    private Integer count_circles;
    private Integer count_sliders;
    private Integer count_spinners;
    private Double cs;
    private String deleted_at;
    private Double drain;
    private Integer hit_length;
    private Boolean is_scoreable;
    private String last_updated;
    private Integer mode_int;
    private Integer passcount;
    private Integer playcount;
    private Integer ranked;
    private String url;

}
