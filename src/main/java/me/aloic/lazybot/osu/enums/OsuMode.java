package me.aloic.lazybot.osu.enums;

import kotlin.Lazy;
import me.aloic.lazybot.exception.LazybotRuntimeException;

public enum OsuMode
{
    Osu(0, "osu"),
    Taiko(1, "taiko"),
    Catch(2, "fruits"),
    Mania(3, "mania"),
    Default(-1, "");

    private final int value;
    private final String describe;

    OsuMode(int value, String describe) {
        this.value = value;
        this.describe = describe;
    }

    public int getValue() {
        return value;
    }

    public String getDescribe() {
        return describe;
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
        if (name == null) throw new LazybotRuntimeException("Null mode provided");
        return switch (name.toLowerCase().trim())
        {
            case "osu", "o", "0", "std", "standard" -> Osu;
            case "taiko", "t", "1", "tk" -> Taiko;
            case "catch", "c", "ctb", "fruits", "fruit", "f", "2" -> Catch;
            case "mania", "m", "3", "mn" -> Mania;
            default -> throw new LazybotRuntimeException("Invalid mode provided: " + name);
        };
    }
    public static org.spring.osu.OsuMode convertMode(String name) {
        if (name == null) throw new LazybotRuntimeException("Null mode provided");
        return switch (name.toLowerCase().trim())
        {
            case "osu", "o", "0", "std", "standard" -> org.spring.osu.OsuMode.Osu;
            case "taiko", "t", "1", "tk" -> org.spring.osu.OsuMode.Taiko;
            case "catch", "c", "ctb", "fruits", "fruit", "f", "2" -> org.spring.osu.OsuMode.Catch;
            case "mania", "m", "3", "mn" -> org.spring.osu.OsuMode.Mania;
            default -> throw new LazybotRuntimeException("Invalid mode provided: " + name);
        };
    }
    @Override
    public String toString() {
        return describe;
    }

}
