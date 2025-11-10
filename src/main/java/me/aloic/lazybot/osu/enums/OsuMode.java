package me.aloic.lazybot.osu.enums;

import me.aloic.lazybot.exception.LazybotRuntimeException;

public enum OsuMode
{
    Osu(0, "osu"),
    Taiko(1, "taiko"),
    Catch(2, "fruits"),
    Mania(3, "mania"),
    StarMoonOsu(4, "smo"),
    StarMoonOTaiko(5, "smt"),
    StarMoonCatch(6, "smf"),
    StarMoonMania(7, "smm"),
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
            case 4 -> StarMoonOsu;
            case 5 -> StarMoonOTaiko;
            case 6 -> StarMoonCatch;
            case 7 -> StarMoonMania;
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
            case "star-moon-osu", "smo", "4" -> StarMoonOsu;
            case "star-moon-taiko", "smt", "5" -> StarMoonOTaiko;
            case "star-moon-catch", "smc", "6" -> StarMoonCatch;
            case "star-moon-mania", "smm", "7" -> StarMoonMania;
            default -> throw new LazybotRuntimeException("无效的模式: " + name);
        };
    }
    public static org.spring.osu.OsuMode convertMode(String name) {
        if (name == null) throw new LazybotRuntimeException("传入模式为空");
        return switch (name.toLowerCase().trim())
        {
            case "osu", "o", "0", "std", "standard" -> org.spring.osu.OsuMode.Osu;
            case "taiko", "t", "1", "tk" -> org.spring.osu.OsuMode.Taiko;
            case "catch", "c", "ctb", "fruits", "fruit", "f", "2" -> org.spring.osu.OsuMode.Catch;
            case "mania", "m", "3", "mn" -> org.spring.osu.OsuMode.Mania;
            default -> throw new LazybotRuntimeException("无效的模式: " + name);
        };
    }
    @Override
    public String toString() {
        return describe;
    }

}
