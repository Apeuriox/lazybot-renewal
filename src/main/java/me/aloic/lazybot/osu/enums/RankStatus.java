package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RankStatus
{
    GRAVEYARD(-2, "graveyard"),
    WIP(-1, "wip"),
    PENDING(0, "pending"),
    RANKED(1, "ranked"),
    APPROVED(2, "approved"),
    QUALIFIED(3, "qualified"),
    LOVED(4, "loved");

    private final Integer value;
    private final String name;


    public static RankStatus fromValue(Integer value)
    {
        return switch (value)
        {
            case -2 -> GRAVEYARD;
            case -1 -> WIP;
            case 0 -> PENDING;
            case 1 -> RANKED;
            case 2 -> APPROVED;
            case 3 -> QUALIFIED;
            case 4 -> LOVED;
            default -> throw new IllegalArgumentException("Invalid value: " + value);
        };
    }
}
