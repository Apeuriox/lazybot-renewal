package me.aloic.lazybot.osu.difficulty.attribute;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OsuDifficultyAttributes extends DifficultyAttributes
{
    public double aim;
    public double speed;
    public double flashlight;
    public double sliderFactor;
    public double speedNoteCount;
    public double aimDifficultStrainCount;
    public double speedDifficultStrainCount;
    public double ar;
    public double od;
    public double hp;
    public double cs;
    public double nCircles;
    public double nSliders;
    public double nLargeTicks;
    public double nSpinners;
    public double stars;
    public double maxCombo;
}
