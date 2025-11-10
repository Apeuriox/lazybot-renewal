package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SupportedSubServer
{
    STABLE(0,"Bancho Stable"),
    LAZER(1,"Bancho Lazer"),
    STAR_MOON(2,"Star Moon");

    private int value;
    private String name;
}
