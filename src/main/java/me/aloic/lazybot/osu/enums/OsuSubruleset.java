package me.aloic.lazybot.osu.enums;

import me.aloic.lazybot.exception.LazybotRuntimeException;

public enum OsuSubruleset
{
    Standard(0, "standard"),
    Relax(1, "relax"),
    Autopilot(2, "autopilot"),
    Default(-1, "");

    private final int value;
    private final String describe;

    OsuSubruleset(int value, String describe) {
        this.value = value;
        this.describe = describe;
    }

    public int getValue() {
        return value;
    }

    public String getDescribe() {
        return describe;
    }

    public static OsuSubruleset getRuleset(int value) {
        return switch (value)
        {
            case 0 -> Standard;
            case 1 -> Relax;
            case 2 -> Autopilot;
            default -> Default;
        };
    }

    public static OsuSubruleset getRuleset(String name) {
        if (name == null) throw new LazybotRuntimeException("传入模式为空");
        return switch (name.toLowerCase().trim())
        {
            case "std", "s", "0", "standard" -> Standard;
            case "relax", "rx", "1", "rl" -> Relax;
            case "ap", "auto", "2", "autopilot" -> Autopilot;
            default -> throw new LazybotRuntimeException("无效的模式: " + name);
        };
    }
    @Override
    public String toString() {
        return describe;
    }

}
