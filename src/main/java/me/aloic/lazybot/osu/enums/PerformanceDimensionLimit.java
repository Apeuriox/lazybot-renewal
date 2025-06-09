package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PerformanceDimensionLimit
{
    JUMP("jump", 1000.0, 7500.0, 11000.0, 0.903),
    FLOW("flow", 400.0, 5500.0, 8000.0,0.691),
    SPEED("speed", 800.0, 5500.0, 7500.0,1.109),
    STAMINA("stamina", 500.0, 300.0, 6200,0.993),
    PRECISION("precision", 300.0, 3700.0, 6000,0.741),
    ACCURACY("accuracy", 600.0, 3000.0, 4000.0,0.891),
    AVERAGE("average", 200.0, 5500.0, 8000.0,0.811);


    private final String name;
    private final double limitRookie;
    private final double limitExpert;
    private final double limitExpertPlus;
    private final double scaleFactor;
}
