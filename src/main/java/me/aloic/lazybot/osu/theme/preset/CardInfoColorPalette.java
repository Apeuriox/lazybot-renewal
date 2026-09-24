package me.aloic.lazybot.osu.theme.preset;

import lombok.Builder;
import lombok.Value;
import me.aloic.lazybot.osu.theme.Color.OKHSL;
import me.aloic.lazybot.util.ColorUtils;

@Value
@Builder
public class CardInfoColorPalette
{
    String leftBar;
    String nameBlock;
    String statsBlock;
    String footerJoin;
    String footerRank;
    String footerLevel;
    String footerAcc;
    String deco;
    String infoText;
    boolean showStripes;
    boolean lowContrast;

    public static CardInfoColorPalette fromRgb(int[] rgb)
    {
        if (rgb == null || rgb.length < 3) {
            return lowDark(0);
        }
        double[] ok = OKHSL.rawFromRgb(rgb[0], rgb[1], rgb[2]);
        int hue = Math.floorMod((int) Math.round(ok[0] * 360.0), 360);
        double s = ok[1];
        double l = ok[2];
        boolean lowContrast = s < 0.21 || l < 0.12 || l > 0.88;
        if (!lowContrast) {
            double lift = 7.0 * clamp01((l - 0.50) / (0.88 - 0.50));
            return vivid(hue, lift);
        }
        return l > 0.50 ? lowLight(hue) : lowDark(hue);
    }

    private static CardInfoColorPalette vivid(int hue, double lift)
    {
        return CardInfoColorPalette.builder()
                .leftBar(fill(hue, -9, 98, 60, lift))
                .nameBlock(fill(hue, 0, 63, 80, lift))
                .statsBlock(fill(hue, 1, 48, 66, lift))
                .footerJoin(fill(hue, 1, 93, 24, lift))
                .footerRank(fill(hue, -10, 77, 42, lift))
                .footerLevel(fill(hue, -10, 85, 56, lift))
                .footerAcc(fill(hue, -10, 100, 68, lift))
                .deco(fill(hue, -11, 28, 34, lift))
                .infoText("#111111")
                .showStripes(false)
                .lowContrast(false)
                .build();
    }

    private static CardInfoColorPalette lowLight(int hue)
    {
        return CardInfoColorPalette.builder()
                .leftBar(fill(hue, -9, 51, 62, 0))
                .nameBlock(fill(hue, 0, 61, 80, 0))
                .statsBlock(fill(hue, 1, 52, 65, 0))
                .footerJoin(fill(hue, 1, 67, 19, 0))
                .footerRank(fill(hue, -10, 33, 33, 0))
                .footerLevel(fill(hue, -10, 38, 44, 0))
                .footerAcc(fill(hue, -10, 67, 48, 0))
                .deco(fill(hue, -8, 28, 34, 0))
                .infoText("#111111")
                .showStripes(false)
                .lowContrast(true)
                .build();
    }

    private static CardInfoColorPalette lowDark(int hue)
    {
        return CardInfoColorPalette.builder()
                .leftBar(fill(hue, -9, 0, 60, 0))
                .nameBlock(fill(hue, 0, 0, 80, 0))
                .statsBlock(fill(hue, 1, 0, 66, 0))
                .footerJoin(fill(hue, 1, 0, 24, 0))
                .footerRank(fill(hue, -10, 0, 42, 0))
                .footerLevel(fill(hue, -10, 0, 56, 0))
                .footerAcc(fill(hue, -10, 0, 68, 0))
                .deco(fill(hue, -11, 0, 34, 0))
                .infoText("#111111")
                .showStripes(true)
                .lowContrast(true)
                .build();
    }

    private static String fill(int hue, int hueOffset, int saturation, int lightness, double lift)
    {
        return ColorUtils.okhsl(ColorUtils.circularHueSubtract(hue, hueOffset), saturation, lightness, lift);
    }

    private static double clamp01(double v)
    {
        if (v < 0) return 0;
        if (v > 1) return 1;
        return v;
    }
}
