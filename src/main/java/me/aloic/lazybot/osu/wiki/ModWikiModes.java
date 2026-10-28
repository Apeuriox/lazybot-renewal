package me.aloic.lazybot.osu.wiki;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ModWikiModes
{
    private static final List<String> ORDER = List.of("osu", "taiko", "fruits", "mania");

    private ModWikiModes()
    {
    }

    public static String canonical(String raw)
    {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "osu", "std", "0" -> "osu";
            case "taiko", "1" -> "taiko";
            case "fruits", "catch", "ctb", "2" -> "fruits";
            case "mania", "3" -> "mania";
            default -> null;
        };
    }

    public static String symbolId(String canonical)
    {
        return switch (canonical) {
            case "osu" -> "osuMode";
            case "taiko" -> "taikoMode";
            case "fruits" -> "fruitMode";
            case "mania" -> "maniaMode";
            default -> "";
        };
    }

    public static double originX(String canonical)
    {
        return switch (canonical) {
            case "osu" -> 782;
            case "taiko" -> 838;
            case "fruits" -> 894;
            case "mania" -> 950;
            default -> 0;
        };
    }

    public static List<String> ordered(Collection<String> modes)
    {
        if (modes == null || modes.isEmpty()) {
            return List.of();
        }
        Set<String> present = new LinkedHashSet<>();
        for (String mode : modes) {
            String canonical = canonical(mode);
            if (canonical != null) {
                present.add(canonical);
            }
        }
        List<String> ordered = new ArrayList<>();
        for (String mode : ORDER) {
            if (present.contains(mode)) {
                ordered.add(mode);
            }
        }
        return List.copyOf(ordered);
    }
}
