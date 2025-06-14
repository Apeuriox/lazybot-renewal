package me.aloic.lazybot.osu.difficulty.attribute;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TaikoDifficultyAttributes extends DifficultyAttributes
{
    public double stamina;
    public double rhythm;

    public double color;

    public double peak;

    public double greatHitWindow;

    public double okHitWindow;

    public double monoStaminaFactor;

    public double stars;

    public int maxCombo;

    public boolean isConvert;
}
