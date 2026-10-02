package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SupportedSubServer
{
    STABLE(0,"Bancho Stable"),
    LAZER(1,"Bancho Lazer"),
    STAR_MOON(2,"Star Moon");

    private final int value;
    private final String name;
}
