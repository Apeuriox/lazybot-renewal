package me.aloic.lazybot.osu.enums;

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
        switch (value) {
            case 0:
                return Osu;
            case 1:
                return Taiko;
            case 2:
                return Catch;
            case 3:
                return Mania;
            default:
                return Default;
        }
    }

    public static OsuMode getMode(String name) {
        if (name == null) return Default;
        switch (name.toLowerCase()) {
            case "osu":
            case "o":
            case "0":
                return Osu;
            case "taiko":
            case "t":
            case "1":
                return Taiko;
            case "catch":
            case "c":
            case "fruits":
            case "f":
            case "2":
                return Catch;
            case "mania":
            case "m":
            case "3":
                return Mania;
            default:
                return Default;
        }
    }

    @Override
    public String toString() {
        return describe;
    }

}
