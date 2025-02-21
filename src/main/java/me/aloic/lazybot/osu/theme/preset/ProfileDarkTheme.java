package me.aloic.lazybot.osu.theme.preset;

import me.aloic.lazybot.osu.theme.Color.HSL;

public class ProfileDarkTheme extends ProfileTheme
{
    private ProfileDarkTheme(){
    }
    private ProfileDarkTheme(int hue) {
        this.setHue(hue);
        this.setHeaderBorderColor(new HSL(hue, 70, 85));
        this.setBorderColor(new HSL(hue, 40, 10));
        this.setBlockColor(new HSL(hue, 12, 10));
        this.setBlockColorLighter(new HSL(hue, 24, 12));
        this.setMainColor(new HSL(hue, 100, 90));
        this.setMainMiddleColor(new HSL(hue, 80, 95));
        this.setHeaderColor(new HSL(hue, 40, 10));
        this.setLevelProgressBackgroundColor(new HSL(hue,8,35));
        this.setModeInactiveColor(new HSL(hue, 0, 90));
        this.setThemeType(ThemeType.DARK);
    }

    public static ProfileDarkTheme createInstance(int hue) {
        return new ProfileDarkTheme(hue);
    }
}
