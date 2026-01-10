package me.aloic.lazybot.osu.utils;

import java.awt.Color;

public class ColorUtil {

    private static final double[] DOMAIN_DIFFICULTY = {9.0, 9.9, 10.6, 11.5, 12.4};
    private static final String[] RANGE_DIFFICULTY = {"#F6F05C", "#FF8068", "#FF4E6F", "#C645B8", "#6563DE", "#6161CF"};
    private static final double[] DOMAIN_STAR = {0.1, 1.25, 2, 2.5, 3.3, 4.2, 4.9, 5.8, 6.7, 7.7, 9};
    private static final String[] RANGE_STAR = {"#4290FB", "#4FC0FF", "#4FFFD5", "#7CFF4F", "#F6F05C", "#FF8068", "#FF4E6F",
            "#C645B8", "#6563DE", "#18158E", "#000000"};
    private static final double GAMMA = 2.2;

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

    //starts with #
    public static String getDifficultyColor(double starRating)
    {
        if (starRating < 6.5) return "#1c1719";
        if (starRating < 9) return "#fed867";
        Color result = getDifficultyColorSpectrum(starRating);
        return String.format("#%02x%02x%02x", result.getRed(), result.getGreen(), result.getBlue());
    }
    //did not start with #
    public static String getDifficultyBackgroundColor(double starRating)
    {
        Color result = getDifficultyBackgroundColorSpectrum(starRating);
        return String.format("%02x%02x%02x", result.getRed(), result.getGreen(), result.getBlue());
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
