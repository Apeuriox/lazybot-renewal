package me.aloic.lazybot.osu.theme.preset;

import lombok.Data;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.theme.Color.HSL;
@Data
public abstract class ProfileTheme
{
    //other types can be considered as one of them
    public enum ThemeType{
        LIGHT,DARK
    }
    private int hue;
    private HSL mainColor;
    private HSL mainMiddleColor;
    private HSL lightFontColor;
    private HSL headerColor;
    private HSL borderColor;
    private HSL headerBorderColor;
    private HSL blockColor;
    private HSL blockColorLighter;
    private HSL levelProgressBackgroundColor;
    private HSL modeInactiveColor;

    private ThemeType themeType;
    public static ProfileTheme getInstance(int type,int hue) {
        if(type== 0) return ProfileLightTheme.createInstance(hue);
        else if (type == 1) return ProfileDarkTheme.createInstance(hue);
        else if (type == 2) return ProfileLighterTheme.createInstance(hue);
        else throw new LazybotRuntimeException("Unknown theme type: " + type);
    }
    public static Integer getTypeInt(String input) {
        if (input == null) throw new LazybotRuntimeException("Null type provided");
        return switch (input.toLowerCase().trim()) {
            case "light", "l", "0","day" -> 0;
            case "dark", "d", "night", "1" -> 1;
            case "lighter", "2" -> 2;
            default -> throw new LazybotRuntimeException("Invalid type provided: " + input);
        };
    }
}
