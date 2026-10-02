package me.aloic.lazybot.osu.utils;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.rosupp.AlgorithmVersion;

import java.util.Locale;

/** User-facing parsing and formatting for rosu-pp algorithm versions. */
public final class RosuAlgorithmVersionUtil
{
    public static final AlgorithmVersion LATEST = AlgorithmVersion.REWORK_20260706;
    public static final String SUPPORTED_ALIASES = "202210, 202411, 202502, 202510, 20260706, latest";

    private RosuAlgorithmVersionUtil() {}

    public static AlgorithmVersion parse(String input)
    {
        if (input == null || input.isBlank()) {
            return LATEST;
        }

        String normalized = input.trim().toLowerCase(Locale.ROOT);
        normalized = normalized.replaceFirst(
                "^(?:@|v|(?:--?)?algorithm=|(?:--?)?algo=)", "");

        return switch (normalized) {
            case "latest", "current", "new", "20260706", "rework-20260706",
                 "202607", "rework-20260706-9a073d2" -> AlgorithmVersion.REWORK_20260706;
            case "202510", "rework-202510", "rework_202510" -> AlgorithmVersion.REWORK_202510;
            case "202502", "rework-202502", "rework_202502",
                 "rework-202502-rosu-pp-3.1.0" -> AlgorithmVersion.REWORK_202502;
            case "202411", "rework-202411", "rework_202411",
                 "rework-202411-rosu-pp-2.0.0" -> AlgorithmVersion.REWORK_202411;
            case "202210", "precsr-202210", "precsr_202210",
                 "precsr-202210-rosu-pp-1.0.0" -> AlgorithmVersion.PRECSR_202210;
            default -> throw new LazybotRuntimeException(
                    "未知的 PP 算法版本: " + input + "。可用版本: " + SUPPORTED_ALIASES);
        };
    }

    public static String shortLabel(AlgorithmVersion version)
    {
        return switch (version) {
            case PRECSR_202210 -> "202210";
            case REWORK_202411 -> "202411";
            case REWORK_202502 -> "202502";
            case REWORK_202510 -> "202510";
            case REWORK_20260706 -> "20260706";
        };
    }
}
