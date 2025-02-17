package me.aloic.lazybot.osu.theme.preset;

import lombok.Data;
import me.aloic.lazybot.osu.theme.Color.HSL;
@Data
public abstract class ProfileTheme
{
    public enum ThemeType{
        LIGHT,DARK
    }
    private int hue;
    private HSL mainColor;
    private HSL mainMiddleColor;
    private HSL lightFontColor;
    private HSL lightHeaderColor;
    private HSL borderColor;
    private HSL headerBorderColor;
    private HSL evenBrighterMainColor;
    private HSL brightMainColor;

    private ThemeType themeType;
}
