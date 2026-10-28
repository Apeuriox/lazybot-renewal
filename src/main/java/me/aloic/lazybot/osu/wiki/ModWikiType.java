package me.aloic.lazybot.osu.wiki;

import java.util.Locale;

/**
 * Colors follow {@code ModWiki.vue} and {@code ModHelper}.
 * Text is the type label, the icon fills the hexagon, and the pill is the incompatible chip.
 */
public enum ModWikiType
{
    REDUCE("降低难度", "#D4FFA6", "#A5CC00", "#66FF66"),
    INCREASE("提升难度", "#FFB0A6", "#FF6666", "#FF6666"),
    CONVERT("转化", "#BB9DF1", "#8C66FF", "#8C66FF"),
    AUTO("自动", "#8DF6E3", "#66CCFF", "#66CCFF"),
    FUN("乐趣", "#FFB3D0", "#FF66B3", "#FF66B3"),
    SYSTEM("系统", "#F6DE8D", "#FFCC24", "#FFCC24"),
    UNKNOWN("", "#E7E7E8", "#9A97A8", "#9A97A8");

    private final String label;
    private final String text;
    private final String icon;
    private final String pill;

    ModWikiType(String label, String text, String icon, String pill)
    {
        this.label = label;
        this.text = text;
        this.icon = icon;
        this.pill = pill;
    }

    public String text()
    {
        return text;
    }

    public String icon()
    {
        return icon;
    }

    public String pill()
    {
        return pill;
    }

    public static ModWikiType fromLabel(String label)
    {
        return fromKey(label);
    }

    public static ModWikiType fromKey(String raw)
    {
        if (raw == null || raw.isBlank()) {
            return UNKNOWN;
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "降低难度", "reduce" -> REDUCE;
            case "提升难度", "increase" -> INCREASE;
            case "转化", "conversion", "convert" -> CONVERT;
            case "自动", "auto", "automation" -> AUTO;
            case "乐趣", "fun" -> FUN;
            case "系统", "system" -> SYSTEM;
            default -> UNKNOWN;
        };
    }

    public static String multiplierColor(int kind)
    {
        return switch (kind) {
            case 0 -> "#D4FFA6";
            case 2 -> "#E5B6A4";
            default -> "#F3F3F3";
        };
    }
}
