package me.aloic.lazybot.osu.enums;

import java.util.Locale;

public enum AccountLinkMethod
{
    MANUAL("manual"),
    OAUTH("oauth");

    private final String databaseValue;

    AccountLinkMethod(String databaseValue)
    {
        this.databaseValue = databaseValue;
    }

    public String databaseValue()
    {
        return databaseValue;
    }

    public static AccountLinkMethod fromDatabaseValue(String value)
    {
        if (value == null) {
            throw new IllegalArgumentException("绑定方式不能为空");
        }
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "manual" -> MANUAL;
            case "oauth" -> OAUTH;
            default -> throw new IllegalArgumentException("不支持的绑定方式: " + value);
        };
    }
}
