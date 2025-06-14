package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PerformancePlusTag
{
    AIM("Aim", "ppJump", 40, 35),
    ACCURATE("Accurate", "ppAccuracy", 80, 55),
    FLOW("Flow", "ppFlow", 45, 37),
    PRECISE("Precise", "ppPrecision", 60, 45),
    SPEEDY("Speed Dog", "ppSpeed", 90, 60),
    ENDURING("Enduring", "ppStamina", 75, 53),
    OMNIPOTENT("Omnipotent", null, 95, 63),
    COMPREHENSIVE("Comprehensive", null, 120, 75),
    POTENTIAL("Potential", null, 76, 53);


    private final String name;
    private final String dimensionField; // 对应 Performance 对象的字段名
    private final Integer elementSize;
    private final Integer anchor;
}
