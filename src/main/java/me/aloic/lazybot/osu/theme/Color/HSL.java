package me.aloic.lazybot.osu.theme.Color;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class HSL
{
    private int hue;
    private int saturation;
    private int lightness;

    @Override
    public String toString() {
        return "hsl("+hue+","+saturation+"%,"+lightness+"%)";
    }

    public static String hslFormat(int hue,int saturation,int lightness) {
        return "hsl("+hue+","+saturation+"%,"+lightness+"%)";
    }
}
