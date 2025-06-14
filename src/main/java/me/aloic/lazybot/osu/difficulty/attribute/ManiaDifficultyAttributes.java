package me.aloic.lazybot.osu.difficulty.attribute;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ManiaDifficultyAttributes extends DifficultyAttributes
{
    public double stars;
    public int nObjects;
    public int nHoldNotes;
    public int maxCombo;
    public boolean isConvert;
}
