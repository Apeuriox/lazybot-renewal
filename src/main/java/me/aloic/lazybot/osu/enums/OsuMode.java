package me.aloic.lazybot.osu.enums;

import lombok.Getter;
import me.aloic.lazybot.exception.LazybotRuntimeException;

@Getter
public enum OsuMode
{
    Osu(0, "osu","s"),
    Taiko(1, "taiko","t"),
    Catch(2, "fruits","f"),
    Mania(3, "mania","m"),
    Default(-1, "","");

    private final int value;
    private final String describe;
    private final String abbr;

    OsuMode(int value, String describe, String abbr) {
        this.value = value;
        this.describe = describe;
        this.abbr = abbr;
    }


    public static OsuMode getMode(int value) {
        return switch (value)
        {
            case 0 -> Osu;
            case 1 -> Taiko;
            case 2 -> Catch;
            case 3 -> Mania;
            default -> Default;
        };
    }

    public static OsuMode getMode(String name) {
        if (name == null) throw new LazybotRuntimeException("传入模式为空");
        return switch (name.toLowerCase().trim())
        {
            case "osu", "o", "0", "std", "standard" -> Osu;
            case "taiko", "t", "1", "tk" -> Taiko;
            case "catch", "c", "ctb", "fruits", "fruit", "f", "2" -> Catch;
            case "mania", "m", "3", "mn" -> Mania;
            default -> throw new LazybotRuntimeException("无效的模式: " + name);
        };
    }
    public static OsuMode convertMode(String name) {
        return getMode(name);
    }
    @Override
    public String toString() {
        return describe;
    }

}
