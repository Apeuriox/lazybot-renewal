package me.aloic.lazybot.osu.enums;

import java.util.Locale;

public enum IdentityPlatform
{
    QQ("qq"),
    DISCORD("discord");

    private final String databaseValue;

    IdentityPlatform(String databaseValue)
    {
        this.databaseValue = databaseValue;
    }

    public String databaseValue()
    {
        return databaseValue;
    }

    public static IdentityPlatform fromDatabaseValue(String value)
    {
        if (value == null) {
            throw new IllegalArgumentException("平台类型不能为空");
        }
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "qq" -> QQ;
            case "discord" -> DISCORD;
            default -> throw new IllegalArgumentException("不支持的平台类型: " + value);
        };
    }
}
