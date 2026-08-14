package me.aloic.lazybot.util;

import de.androidpit.colorthief.ColorThief;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import me.aloic.lazybot.osu.theme.Color.HSL;

//I know it's copied code from CommonTool. We're migrating it to there but it takes time.
public class ColorUtils
{
    // Circular hue subtraction: (hue - offset) wrapped to [0, 360)
    public static int circularHueSubtract(int hue, int offset) {
        return ((hue - offset) % 360 + 360) % 360;
    }

    // Check if a hue falls in the "warm" range (red–yellow–orange, 0-60 or 300-360)
    public static boolean isWarmColor(int hue) {
        hue = (hue % 360 + 360) % 360;
        return hue <= 60 || hue >= 300;
    }

    // Compute a "hue-safe" value: if >360 return a fallback default (75), otherwise return as-is
    public static int safeHue(int hue) {
        return hue > 360 ? 75 : hue;
    }
    // Convert RGB int array to HSL object with detailed saturation/lightness calculation
    public static HSL rgbToHslDetailed(int[] rgb) {
        double r = rgb[0] / 255.0;
        double g = rgb[1] / 255.0;
        double b = rgb[2] / 255.0;
        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double delta = max - min;
        double l = (max + min) / 2.0;
        double h = 0, s = 0;
        if (delta != 0) {
            s = delta / (1 - Math.abs(2 * l - 1));
            if (max == r) h = 60 * ((g - b) / delta % 6);
            else if (max == g) h = 60 * ((b - r) / delta + 2);
            else if (max == b) h = 60 * ((r - g) / delta + 4);
            if (h < 0) h += 360;
        }
        return new HSL((int) h, (int) (s * 100), (int) (l * 100));
    }

    private static final double[] DOMAIN_DIFFICULTY = {9.0, 9.9, 10.6, 11.5, 12.4};
    private static final String[] RANGE_DIFFICULTY = {"#F6F05C", "#FF8068", "#FF4E6F", "#C645B8", "#6563DE", "#6161CF"};
    private static final double[] DOMAIN_STAR = {0.1, 1.25, 2, 2.5, 3.3, 4.2, 4.9, 5.8, 6.7, 7.7, 9};
    private static final String[] RANGE_STAR = {"#4290FB", "#4FC0FF", "#4FFFD5", "#7CFF4F", "#F6F05C", "#FF8068", "#FF4E6F",
            "#C645B8", "#6563DE", "#18158E", "#000000"};
    private static final double GAMMA = 2.2;

    public static Integer getDominantHueColorThief(File imageFile)
    {
        try{
            return rgbToHue(getDominantColorColorThief(imageFile));
        }
        catch (Exception e) {
            throw new RuntimeException("计算主体色相失败: " + e.getMessage());
        }

    }

    public static int[] hexToRgb(String hex) {
        if (hex.length() != 6) {
            throw new IllegalArgumentException("HEX颜色必须是6位字符");
        }
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new int[] { r, g, b };
    }

    public static Integer rgbToHue(int[] rgb) {
        double r=rgb[0];
        double g=rgb[1];
        double b=rgb[2];
        r/= 255.0;
        g /= 255.0;
        b /= 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double delta = max - min;
        double l = (max + min) / 2;
        if (l<0.05) return 361;
        if (l>0.92) return 361;
        double s = 0;
        if (delta != 0) {
            s = delta / (1 - Math.abs(2 * l - 1));
        }
        if (s<0.07) return 361;

        double h = 0;
        if (delta != 0) {
            if (max == r) {
                h = 60 * ((g - b) / delta % 6);
            } else if (max == g) {
                h = 60 * ((b - r) / delta + 2);
            } else if (max == b) {
                h = 60 * ((r - g) / delta + 4);
            }
        }
        if (h < 0) {
            h += 360;
        }
        return (int) h;
    }

    public static int[] getDominantColorColorThief(File imageFile) throws IOException {
        try{
            BufferedImage image = ImageIO.read(imageFile);
            return ColorThief.getColor(image);
        }
        catch (Exception e)
        {
            return new int[]{0,0,0};
        }
    }
    //starts with #
    public static String getDifficultyColor(double starRating)
    {
        if (starRating < 6.5) return "#1c1719";
        if (starRating < 9) return "#fed867";
        Color result = getDifficultyColorSpectrum(starRating);
        return String.format("#%02x%02x%02x", result.getRed(), result.getGreen(), result.getBlue());
    }
    public static String getDifficultyBackgroundColor(double starRating)
    {
        Color result = getDifficultyBackgroundColorSpectrum(starRating);
        return String.format("#%02x%02x%02x", result.getRed(), result.getGreen(), result.getBlue());
    }


    public static Color getDifficultyColorSpectrum(double starRating) {
        // 1. Clamp 限制范围 (对应 d3.clamp(true))
        if (starRating <= DOMAIN_DIFFICULTY[0]) return Color.decode(RANGE_DIFFICULTY[0]);
        if (starRating >= DOMAIN_DIFFICULTY[DOMAIN_DIFFICULTY.length - 1]) return Color.decode(RANGE_DIFFICULTY[RANGE_DIFFICULTY.length - 1]);

        // 2. 寻找区间
        int i = 1;
        while (starRating > DOMAIN_DIFFICULTY[i]) {
            i++;
        }

        // 3. 计算区间内的比例 t (0.0 到 1.0)
        double t = (starRating - DOMAIN_DIFFICULTY[i - 1]) / (DOMAIN_DIFFICULTY[i] - DOMAIN_DIFFICULTY[i - 1]);

        // 4. 插值颜色
        return interpolate(RANGE_DIFFICULTY[i - 1], RANGE_DIFFICULTY[i], t);
    }
    public static Color getDifficultyBackgroundColorSpectrum(double starRating) {
        // 1. Clamp 限制范围 (对应 d3.clamp(true))
        if (starRating <= DOMAIN_STAR[0]) return Color.decode(RANGE_STAR[0]);
        if (starRating >= DOMAIN_STAR[DOMAIN_STAR.length - 1]) return Color.decode(RANGE_STAR[RANGE_STAR.length - 1]);

        // 2. 寻找区间
        int i = 1;
        while (starRating > DOMAIN_STAR[i]) {
            i++;
        }

        // 3. 计算区间内的比例 t (0.0 到 1.0)
        double t = (starRating - DOMAIN_STAR[i - 1]) / (DOMAIN_STAR[i] - DOMAIN_STAR[i - 1]);

        // 4. 插值颜色
        return interpolate(RANGE_STAR[i - 1], RANGE_STAR[i], t);
    }

    private static Color interpolate(String startHex, String endHex, double t) {
        Color c1 = Color.decode(startHex);
        Color c2 = Color.decode(endHex);

        // 对应 d3.interpolateRgb.gamma(2.2)
        // 使用伽马校正进行线性插值，使色彩过渡更自然（避免中间变灰）
        int r = interpolateGamma(c1.getRed(), c2.getRed(), t);
        int g = interpolateGamma(c1.getGreen(), c2.getGreen(), t);
        int b = interpolateGamma(c1.getBlue(), c2.getBlue(), t);

        return new Color(r, g, b);
    }

    private static int interpolateGamma(int v1, int v2, double t) {
        // 公式: Math.pow((1-t)*v1^gamma + t*v2^gamma, 1/gamma)
        double val = Math.pow(
                (1 - t) * Math.pow(v1, GAMMA) + t * Math.pow(v2, GAMMA),
                1.0 / GAMMA
        );
        return (int) Math.round(val);
    }

}
