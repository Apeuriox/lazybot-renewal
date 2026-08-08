package me.aloic.lazybot.osu.enums;

import java.util.Locale;

public enum OsuServer
{
    BANCHO("bancho"),
    STAR_MOON("star_moon");

    private final String databaseValue;

    OsuServer(String databaseValue)
    {
        this.databaseValue = databaseValue;
    }

    public String databaseValue()
    {
        return databaseValue;
    }

    public static OsuServer fromDatabaseValue(String value)
    {
        if (value == null) {
            throw new IllegalArgumentException("服务器类型不能为空");
        }
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "bancho" -> BANCHO;
            case "star_moon" -> STAR_MOON;
            default -> throw new IllegalArgumentException("不支持的服务器类型: " + value);
        };
    }
}
