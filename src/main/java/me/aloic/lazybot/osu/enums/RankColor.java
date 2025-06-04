package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RankColor
{
    A("#88DA20", "#36570d", "#45e06d"),
    B("#e9b941", "#553a2b", "#3baedc"),
    C("#fa8a59", "#473625", "#9352d5"),
    D("#f55757", "#512525", "#e43350"),
    S("#02b5c3", "#ffd362", "#ffd25f"),
    X( "#ce1c9d", "#ffd362", "#ffd464"),
    SH("#02b5c3", "#ddf3f9","#ffd25f"),
    XH("#ce1c9d", "#ddf3f9","#c9eaf5"),
    F("#3a015b", "#cc6faa","#892f2a");

    private final String backgroundColorPeppyHEX;
    private final String iconColorPeppyHEX;
    private final String darkRankColorHEX;


    public static RankColor fromString(String rank) {
        try {
            return RankColor.valueOf(rank);
        } catch (IllegalArgumentException e) {
            return F;
        }
    }
}
