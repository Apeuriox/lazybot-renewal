package me.aloic.lazybot.osu.dao.entity.dto.ppDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Difficulty implements Serializable
{
    private Integer mode;
    private Double stars;
    private Boolean isConvert;
    private Double aim;
    private Double speed;
    private Double flashlight;
    private Double sliderFactor;
    private Double speedNoteCount;
    private Double od;
    private Double hp;
    private Integer nCircles;
    private Integer nSliders;
    private Integer nSpinners;
    private Double ar;
    private Integer maxCombo;
}
