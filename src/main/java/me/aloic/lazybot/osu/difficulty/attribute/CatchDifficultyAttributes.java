package me.aloic.lazybot.osu.difficulty.attribute;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CatchDifficultyAttributes extends DifficultyAttributes
{
    public double stars;
    public double ar;
    public int nFruits;
    public int nDroplets;
    public int nTinyDroplets;
    public boolean isConvert;
}
