package me.aloic.lazybot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GeometryDifficulty
{
    EasyDemon(3, "Easy Demon", true),
    MediumDemon(4, "Medium Demon",true),
    HardDemon(0, "Hard Demon", true),
    InsaneDemon(5, "Insane Demon", true),
    ExtremeDemon(6, "Extreme Demon", true),
    Demon(2, "Demon", true),
    Easy(10, "Easy", false),
    Normal(20, "Normal", false),
    Hard(30, "Hard", false),
    Harder(40, "Harder", false),
    Insane(50, "Insane", false),
    Auto(1, "Auto", false),
    Unrated(-1, "Unrated", false),
    Unknown(-1, "Unknown", false);

    private final int value;
    private final String describe;
    private boolean isDemonDifficulty;



    public static GeometryDifficulty getGeometryDifficulty(int value,boolean isDemonDifficulty)
    {
            if (isDemonDifficulty)
            {
                return switch (value)
                {
                    case 3 -> EasyDemon;
                    case 4 -> MediumDemon;
                    case 0 -> HardDemon;
                    case 5 -> InsaneDemon;
                    case 6 -> ExtremeDemon;
                    default -> Demon;
                };
            }
            return switch (value)
            {
                case 1 -> Auto;
                case 0 -> Unrated;
                case 10 -> Easy;
                case 20 -> Normal;
                case 30 -> Hard;
                case 40 -> Harder;
                case 50 -> Insane;
                default -> Unknown;
            };
    }
}
