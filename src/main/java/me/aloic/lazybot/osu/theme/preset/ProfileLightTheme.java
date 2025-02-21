package me.aloic.lazybot.osu.theme.preset;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aloic.lazybot.osu.theme.Color.HSL;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfileLightTheme extends ProfileTheme
{

    private ProfileLightTheme(){
    }
    private ProfileLightTheme(int hue) {
        this.setHue(hue);
        this.setMainColor(new HSL(hue, 60, 43));
        this.setMainMiddleColor(new HSL(hue, 84, 68));
        this.setLightFontColor(new HSL(hue, 100, 68));
        this.setHeaderColor(new HSL(hue, 39, 93));
        this.setHeaderBorderColor(new HSL(hue, 93, 44));
        this.setBorderColor(new HSL(hue, 93, 82));
        this.setBlockColor(new HSL(hue, 12, 95));
        this.setBlockColorLighter(new HSL(hue, 27, 92));
        this.setLevelProgressBackgroundColor(new HSL(hue, 0, 87));
        this.setModeInactiveColor(new HSL(hue, 0, 31));
        this.setThemeType(ThemeType.LIGHT);
    }

    public static ProfileLightTheme createInstance(int hue) {
        return new ProfileLightTheme(hue);
    }
}
