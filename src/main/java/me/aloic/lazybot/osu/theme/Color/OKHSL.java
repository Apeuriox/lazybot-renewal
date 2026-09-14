package me.aloic.lazybot.osu.theme.Color;

import lombok.Data;

/**
 * OKHSL with HSL-like S% / L% (Björn Ottosson). {@link #toString()} emits {@code #RRGGBB}
 * because resvg does not parse {@code okhsl()}/{@code oklch()}.
 */
@Data
public class OKHSL
{
    private int hue;
    private int saturation;
    private int lightness;

    public OKHSL(int hue, int saturation, int lightness)
    {
        this.hue = Math.floorMod(hue, 360);
        this.saturation = saturation;
        this.lightness = lightness;
    }

    @Override
    public String toString()
    {
        return toHex(hue, saturation, lightness);
    }

    public static String toHex(int hue, int saturation, int lightness)
    {
        int[] rgb = toRgb(hue, saturation, lightness);
        return String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
    }

    public static int[] toRgb(int hue, int saturation, int lightness)
    {
        double h = Math.floorMod(hue, 360) / 360.0;
        double s = clamp01(saturation / 100.0);
        double l = clamp01(lightness / 100.0);
        double[] rgb = okhslToSrgb(h, s, l);
        return new int[] { toByte(rgb[0]), toByte(rgb[1]), toByte(rgb[2]) };
    }

    public static OKHSL fromRgb(int[] rgb)
    {
        return fromRgb(rgb[0], rgb[1], rgb[2]);
    }

    public static OKHSL fromRgb(int r, int g, int b)
    {
        double[] hsl = srgbToOkhsl(r, g, b);
        int hue = (int) Math.round(hsl[0] * 360.0);
        int sat = (int) Math.round(hsl[1] * 100.0);
        int light = (int) Math.round(hsl[2] * 100.0);
        return new OKHSL(hue, clampInt(sat, 0, 100), clampInt(light, 0, 100));
    }

    private static double[] okhslToSrgb(double h, double s, double l)
    {
        if (l >= 1.0)
        {
            return new double[] { 255, 255, 255 };
        }
        if (l <= 0.0)
        {
            return new double[] { 0, 0, 0 };
        }

        double a_ = Math.cos(2 * Math.PI * h);
        double b_ = Math.sin(2 * Math.PI * h);
        double L = toeInv(l);
        double[] cs = getCs(L, a_, b_);
        double C0 = cs[0];
        double Cmid = cs[1];
        double Cmax = cs[2];

        double C;
        if (s < 0.8)
        {
            double t = 1.25 * s;
            double k1 = 0.8 * C0;
            double k2 = 1 - k1 / Cmid;
            C = t * k1 / (1 - k2 * t);
        }
        else
        {
            double t = 5 * (s - 0.8);
            double k0 = Cmid;
            double k1 = 0.2 * Cmid * Cmid * 1.25 * 1.25 / C0;
            double k2 = 1 - k1 / (Cmax - Cmid);
            C = k0 + t * k1 / (1 - k2 * t);
        }

        double[] rgb = oklabToLinearSrgb(L, C * a_, C * b_);
        return new double[] {
                255 * srgbTransfer(rgb[0]),
                255 * srgbTransfer(rgb[1]),
                255 * srgbTransfer(rgb[2])
        };
    }

    private static double[] srgbToOkhsl(int r8, int g8, int b8)
    {
        double[] lab = linearSrgbToOklab(
                srgbTransferInv(r8 / 255.0),
                srgbTransferInv(g8 / 255.0),
                srgbTransferInv(b8 / 255.0)
        );
        double C = Math.hypot(lab[1], lab[2]);
        double L = lab[0];
        if (C < 1e-8)
        {
            return new double[] { 0, 0, toe(L) };
        }

        double a_ = lab[1] / C;
        double b_ = lab[2] / C;
        double h = 0.5 + 0.5 * Math.atan2(-lab[2], -lab[1]) / Math.PI;
        double[] cs = getCs(L, a_, b_);
        double C0 = cs[0];
        double Cmid = cs[1];
        double Cmax = cs[2];

        double sat;
        if (C < Cmid)
        {
            double k1 = 0.8 * C0;
            double k2 = 1 - k1 / Cmid;
            double t = C / (k1 + k2 * C);
            sat = t * 0.8;
        }
        else
        {
            double k0 = Cmid;
            double k1 = 0.2 * Cmid * Cmid * 1.25 * 1.25 / C0;
            double k2 = 1 - k1 / (Cmax - Cmid);
            double t = (C - k0) / (k1 + k2 * (C - k0));
            sat = 0.8 + 0.2 * t;
        }
        return new double[] { h, sat, toe(L) };
    }

    private static double srgbTransfer(double a)
    {
        if (a <= 0) return 0;
        return a >= 0.0031308 ? 1.055 * Math.pow(a, 1.0 / 2.4) - 0.055 : 12.92 * a;
    }

    private static double srgbTransferInv(double a)
    {
        return a > 0.04045 ? Math.pow((a + 0.055) / 1.055, 2.4) : a / 12.92;
    }

    private static double[] linearSrgbToOklab(double r, double g, double b)
    {
        double l = 0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b;
        double m = 0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b;
        double s = 0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b;
        double l_ = Math.cbrt(l);
        double m_ = Math.cbrt(m);
        double s_ = Math.cbrt(s);
        return new double[] {
                0.2104542553 * l_ + 0.7936177850 * m_ - 0.0040720468 * s_,
                1.9779984951 * l_ - 2.4285922050 * m_ + 0.4505937099 * s_,
                0.0259040371 * l_ + 0.7827717662 * m_ - 0.8086757660 * s_
        };
    }

    private static double[] oklabToLinearSrgb(double L, double a, double b)
    {
        double l_ = L + 0.3963377774 * a + 0.2158037573 * b;
        double m_ = L - 0.1055613458 * a - 0.0638541728 * b;
        double s_ = L - 0.0894841775 * a - 1.2914855480 * b;
        double l = l_ * l_ * l_;
        double m = m_ * m_ * m_;
        double s = s_ * s_ * s_;
        return new double[] {
                +4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
                -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
                -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s
        };
    }

    private static double toe(double x)
    {
        final double k1 = 0.206;
        final double k2 = 0.03;
        final double k3 = (1 + k1) / (1 + k2);
        return 0.5 * (k3 * x - k1 + Math.sqrt((k3 * x - k1) * (k3 * x - k1) + 4 * k2 * k3 * x));
    }

    private static double toeInv(double x)
    {
        final double k1 = 0.206;
        final double k2 = 0.03;
        final double k3 = (1 + k1) / (1 + k2);
        return (x * x + k1 * x) / (k3 * (x + k2));
    }

    private static double computeMaxSaturation(double a, double b)
    {
        double k0, k1, k2, k3, k4, wl, wm, ws;
        if (-1.88170328 * a - 0.80936493 * b > 1)
        {
            k0 = 1.19086277; k1 = 1.76576728; k2 = 0.59662641; k3 = 0.75515197; k4 = 0.56771245;
            wl = 4.0767416621; wm = -3.3077115913; ws = 0.2309699292;
        }
        else if (1.81444104 * a - 1.19445276 * b > 1)
        {
            k0 = 0.73956515; k1 = -0.45954404; k2 = 0.08285427; k3 = 0.12541070; k4 = 0.14503204;
            wl = -1.2684380046; wm = 2.6097574011; ws = -0.3413193965;
        }
        else
        {
            k0 = 1.35733652; k1 = -0.00915799; k2 = -1.15130210; k3 = -0.50559606; k4 = 0.00692167;
            wl = -0.0041960863; wm = -0.7034186147; ws = 1.7076147010;
        }

        double S = k0 + k1 * a + k2 * b + k3 * a * a + k4 * a * b;
        double kl = 0.3963377774 * a + 0.2158037573 * b;
        double km = -0.1055613458 * a - 0.0638541728 * b;
        double ks = -0.0894841775 * a - 1.2914855480 * b;

        double l_ = 1 + S * kl;
        double m_ = 1 + S * km;
        double s_ = 1 + S * ks;
        double l = l_ * l_ * l_;
        double m = m_ * m_ * m_;
        double s = s_ * s_ * s_;
        double lDs = 3 * kl * l_ * l_;
        double mDs = 3 * km * m_ * m_;
        double sDs = 3 * ks * s_ * s_;
        double lDs2 = 6 * kl * kl * l_;
        double mDs2 = 6 * km * km * m_;
        double sDs2 = 6 * ks * ks * s_;
        double f = wl * l + wm * m + ws * s;
        double f1 = wl * lDs + wm * mDs + ws * sDs;
        double f2 = wl * lDs2 + wm * mDs2 + ws * sDs2;
        S = S - f * f1 / (f1 * f1 - 0.5 * f * f2);
        return S;
    }

    private static double[] findCusp(double a, double b)
    {
        double SCusp = computeMaxSaturation(a, b);
        double[] rgbAtMax = oklabToLinearSrgb(1, SCusp * a, SCusp * b);
        double LCusp = Math.cbrt(1 / Math.max(Math.max(rgbAtMax[0], rgbAtMax[1]), rgbAtMax[2]));
        return new double[] { LCusp, LCusp * SCusp };
    }

    private static double findGamutIntersection(double a, double b, double L1, double C1, double L0, double[] cusp)
    {
        double t;
        if ((L1 - L0) * cusp[1] - (cusp[0] - L0) * C1 <= 0)
        {
            t = cusp[1] * L0 / (C1 * cusp[0] + cusp[1] * (L0 - L1));
        }
        else
        {
            t = cusp[1] * (L0 - 1) / (C1 * (cusp[0] - 1) + cusp[1] * (L0 - L1));
            double dL = L1 - L0;
            double dC = C1;
            double kl = 0.3963377774 * a + 0.2158037573 * b;
            double km = -0.1055613458 * a - 0.0638541728 * b;
            double ks = -0.0894841775 * a - 1.2914855480 * b;
            double lDt = dL + dC * kl;
            double mDt = dL + dC * km;
            double sDt = dL + dC * ks;

            double L = L0 * (1 - t) + t * L1;
            double C = t * C1;
            double l_ = L + C * kl;
            double m_ = L + C * km;
            double s_ = L + C * ks;
            double l = l_ * l_ * l_;
            double m = m_ * m_ * m_;
            double s = s_ * s_ * s_;
            double ldt = 3 * lDt * l_ * l_;
            double mdt = 3 * mDt * m_ * m_;
            double sdt = 3 * sDt * s_ * s_;
            double ldt2 = 6 * lDt * lDt * l_;
            double mdt2 = 6 * mDt * mDt * m_;
            double sdt2 = 6 * sDt * sDt * s_;

            double rRes = 4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s - 1;
            double r1 = 4.0767416621 * ldt - 3.3077115913 * mdt + 0.2309699292 * sdt;
            double r2 = 4.0767416621 * ldt2 - 3.3077115913 * mdt2 + 0.2309699292 * sdt2;
            double uR = r1 / (r1 * r1 - 0.5 * rRes * r2);
            double tR = -rRes * uR;

            double gRes = -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s - 1;
            double g1 = -1.2684380046 * ldt + 2.6097574011 * mdt - 0.3413193965 * sdt;
            double g2 = -1.2684380046 * ldt2 + 2.6097574011 * mdt2 - 0.3413193965 * sdt2;
            double uG = g1 / (g1 * g1 - 0.5 * gRes * g2);
            double tG = -gRes * uG;

            double bRes = -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s - 1;
            double b1 = -0.0041960863 * ldt - 0.7034186147 * mdt + 1.7076147010 * sdt;
            double b2 = -0.0041960863 * ldt2 - 0.7034186147 * mdt2 + 1.7076147010 * sdt2;
            double uB = b1 / (b1 * b1 - 0.5 * bRes * b2);
            double tB = -bRes * uB;

            tR = uR >= 0 ? tR : 1e6;
            tG = uG >= 0 ? tG : 1e6;
            tB = uB >= 0 ? tB : 1e6;
            t += Math.min(tR, Math.min(tG, tB));
        }
        return t;
    }

    private static double[] getCs(double L, double a_, double b_)
    {
        double[] cusp = findCusp(a_, b_);
        double Cmax = findGamutIntersection(a_, b_, L, 1, L, cusp);
        double STMaxS = cusp[1] / cusp[0];
        double STMaxT = cusp[1] / (1 - cusp[0]);
        double k = Cmax / Math.min(L * STMaxS, (1 - L) * STMaxT);

        double Smid = 0.11516993 + 1 / (
                7.44778970 + 4.15901240 * b_
                        + a_ * (-2.19557347 + 1.75198401 * b_
                        + a_ * (-2.13704948 - 10.02301043 * b_
                        + a_ * (-4.24894561 + 5.38770819 * b_ + 4.69891013 * a_)))
        );
        double Tmid = 0.11239642 + 1 / (
                1.61320320 - 0.68124379 * b_
                        + a_ * (0.40370612 + 0.90148123 * b_
                        + a_ * (-0.27087943 + 0.61223990 * b_
                        + a_ * (0.00299215 - 0.45399568 * b_ - 0.14661872 * a_)))
        );

        double Ca = L * Smid;
        double Cb = (1 - L) * Tmid;
        double Cmid = 0.9 * k * Math.sqrt(Math.sqrt(1 / (1 / (Ca * Ca * Ca * Ca) + 1 / (Cb * Cb * Cb * Cb))));

        Ca = L * 0.4;
        Cb = (1 - L) * 0.8;
        double C0 = Math.sqrt(1 / (1 / (Ca * Ca) + 1 / (Cb * Cb)));
        return new double[] { C0, Cmid, Cmax };
    }

    private static double clamp01(double v)
    {
        if (v < 0) return 0;
        if (v > 1) return 1;
        return v;
    }

    private static int clampInt(int v, int min, int max)
    {
        return Math.max(min, Math.min(max, v));
    }

    private static int toByte(double v)
    {
        int n = (int) Math.round(v);
        if (n < 0) return 0;
        if (n > 255) return 255;
        return n;
    }
}
