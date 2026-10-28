package me.aloic.lazybot.osu.wiki;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public record ModWikiDocument(
        String name,
        String acronym,
        Rank rank,
        List<String> modes,
        List<String> unrankedModes,
        String type,
        List<Multiplier> multipliers,
        String hotkey,
        List<Effect> effects,
        List<Shot> shots,
        List<ModRef> incompatible,
        String extra,
        ModWikiIcon icon)
{
    public ModWikiDocument
    {
        name = name == null ? "" : name.trim();
        acronym = acronym == null ? "" : acronym.trim();
        if (acronym.isBlank() || !acronym.matches("[A-Za-z0-9]+")) {
            throw new IllegalArgumentException("缩写无效: " + acronym);
        }
        rank = rank == null ? Rank.UNRANKED : rank;
        type = type == null ? "" : type.trim();
        hotkey = hotkey == null || hotkey.isBlank() ? "无" : hotkey.trim();
        extra = extra == null || extra.isBlank() ? null : extra.trim();
        modes = modes == null ? List.of() : List.copyOf(modes);
        unrankedModes = unrankedModes == null ? List.of() : List.copyOf(unrankedModes);
        multipliers = multipliers == null ? List.of() : List.copyOf(multipliers);
        effects = effects == null ? List.of() : List.copyOf(effects);
        shots = shots == null ? List.of() : List.copyOf(shots);
        incompatible = incompatible == null ? List.of() : List.copyOf(incompatible);
    }

    public record Multiplier(List<String> modes, String text, int kind)
    {
        public Multiplier
        {
            modes = modes == null ? List.of() : List.copyOf(modes);
            text = text == null ? "" : text.trim();
        }
    }

    public record Effect(List<String> modes, String text)
    {
        public Effect
        {
            modes = modes == null ? List.of() : List.copyOf(modes);
            text = text == null ? "" : text.trim();
        }
    }

    public record Shot(String src, String label, Integer width, Integer height)
    {
    }

    public record ModRef(String type, String name)
    {
    }

    public enum Rank
    {
        RANKED,
        PARTIAL,
        UNRANKED;

        @JsonCreator
        public static Rank from(String raw)
        {
            if (raw == null || raw.isBlank()) {
                return UNRANKED;
            }
            return switch (raw.trim().toUpperCase(Locale.ROOT).replace(' ', '_')) {
                case "RANKED", "RANK", "0" -> RANKED;
                case "PARTIAL", "PARTLY", "PARTIAL_RANKED", "PARTLY_RANKED", "1" -> PARTIAL;
                case "UNRANKED", "2" -> UNRANKED;
                default -> throw new IllegalArgumentException("无法识别的排位状态: " + raw);
            };
        }

        public static Rank fromNumber(int value)
        {
            return switch (value) {
                case 0 -> RANKED;
                case 1 -> PARTIAL;
                case 2 -> UNRANKED;
                default -> throw new IllegalArgumentException("无法识别的排位状态: " + value);
            };
        }
    }

    public static List<String> canonicalModes(List<String> modes)
    {
        if (modes == null || modes.isEmpty()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (String mode : modes) {
            if (mode == null || mode.isBlank()) {
                continue;
            }
            String canonical = ModWikiModes.canonical(mode);
            if (canonical == null) {
                throw new IllegalArgumentException("无法识别的模式: " + mode);
            }
            if (!result.contains(canonical)) {
                result.add(canonical);
            }
        }
        return List.copyOf(result);
    }
}
