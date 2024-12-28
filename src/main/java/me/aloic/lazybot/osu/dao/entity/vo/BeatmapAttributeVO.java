package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BeatmapAttributeVO
{
    private Double ar;
    private Double od;
    private Double cs;
    private Double hp;
    private Double bpm;
    private String mode;
    private Integer length;
}
