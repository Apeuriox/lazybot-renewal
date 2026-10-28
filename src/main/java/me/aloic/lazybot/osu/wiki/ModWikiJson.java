package me.aloic.lazybot.osu.wiki;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads the mod document used by the Vue wiki.
 * {@code ranked} is 0 ranked, 1 partly ranked, 2 unranked.
 * {@code score_multiplier} is either a string plus {@code multiplier_type}, or an array of
 * {@code {mode, multiplier, type}}. {@code effect.mode} is one mode index or an array.
 * {@code unranked_mode} null means the partly-ranked note is omitted.
 */
public final class ModWikiJson
{
    private ModWikiJson()
    {
    }

    public static ModWikiDocument read(JsonNode root)
    {
        if (root == null || !root.isObject()) {
            throw new IllegalArgumentException("Mod 页面不是对象");
        }
        return new ModWikiDocument(
                text(root, "name"),
                firstText(root, "acronym", "abbreviation"),
                rank(root.get("ranked")),
                modes(first(root, "compatible_mode", "modes")),
                unrankedModes(root.get("unranked_mode")),
                text(root, "type"),
                multipliers(root),
                firstText(root, "default_key", "hotkey"),
                effects(first(root, "effect", "effects")),
                shots(root),
                incompatible(first(root, "incompatible_mod", "incompatible")),
                extra(root),
                icon(root.get("icon")));
    }

    private static ModWikiIcon icon(JsonNode node)
    {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return iconFromMarkup(node.asText(), null, "");
        }
        if (!node.isObject()) {
            throw new IllegalArgumentException("icon 格式无法识别");
        }
        double[] explicit = node.hasNonNull("viewBox") ? viewBox(node.get("viewBox")) : null;
        String transform = text(node, "transform");
        if (node.hasNonNull("markup")) {
            return iconFromMarkup(node.get("markup").asText(), explicit, transform);
        }
        StringBuilder markup = new StringBuilder();
        if (node.hasNonNull("d")) {
            markup.append(pathElement(node.get("d").asText(), text(node, "rule")));
        }
        JsonNode paths = node.get("paths");
        if (paths != null && paths.isArray()) {
            for (JsonNode path : paths) {
                if (path == null || path.isNull()) {
                    continue;
                }
                if (path.isTextual()) {
                    markup.append(pathElement(path.asText(), ""));
                }
                else if (path.isObject()) {
                    markup.append(pathElement(text(path, "d"), text(path, "rule")));
                }
            }
        }
        if (markup.isEmpty()) {
            return null;
        }
        double[] box = explicit == null ? new double[]{0, 0, 20, 20} : explicit;
        return new ModWikiIcon(markup.toString(), box[0], box[1], box[2], box[3], transform);
    }

    private static ModWikiIcon iconFromMarkup(String raw, double[] explicit, String transform)
    {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String markup = raw.trim();
        double[] fromSvg = null;
        if (markup.startsWith("<svg")) {
            fromSvg = viewBox(attribute(markup, "viewBox"));
            int open = markup.indexOf('>');
            int close = markup.lastIndexOf("</svg>");
            if (open >= 0 && close > open) {
                markup = markup.substring(open + 1, close).trim();
            }
        }
        double[] box = explicit != null ? explicit : fromSvg != null ? fromSvg : new double[]{0, 0, 20, 20};
        return new ModWikiIcon(markup, box[0], box[1], box[2], box[3], transform);
    }

    private static String pathElement(String d, String rule)
    {
        if (d == null || d.isBlank()) {
            return "";
        }
        String fillRule = rule == null || rule.isBlank() ? "nonzero" : rule.trim();
        return "<path d=\"" + xml(d) + "\" fill=\"black\" fill-rule=\"" + xml(fillRule) + "\"/>";
    }

    private static String xml(String value)
    {
        return value.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String attribute(String markup, String name)
    {
        int key = markup.indexOf(name + "=\"");
        if (key < 0) {
            return "";
        }
        int start = key + name.length() + 2;
        int end = markup.indexOf('"', start);
        return end < 0 ? "" : markup.substring(start, end);
    }

    private static double[] viewBox(JsonNode node)
    {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return viewBox(node.asText());
        }
        if (node.isArray() && node.size() == 4) {
            return new double[]{node.get(0).asDouble(), node.get(1).asDouble(), node.get(2).asDouble(), node.get(3).asDouble()};
        }
        throw new IllegalArgumentException("viewBox 需要四个数字");
    }

    private static double[] viewBox(String raw)
    {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String[] parts = raw.trim().split("[,\\s]+");
        if (parts.length != 4) {
            throw new IllegalArgumentException("viewBox 需要四个数字");
        }
        return new double[]{
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3])
        };
    }

    private static ModWikiDocument.Rank rank(JsonNode node)
    {
        if (node == null || node.isNull()) {
            return ModWikiDocument.Rank.UNRANKED;
        }
        if (node.isNumber()) {
            return ModWikiDocument.Rank.fromNumber(node.intValue());
        }
        return ModWikiDocument.Rank.from(node.asText());
    }

    private static List<String> unrankedModes(JsonNode node)
    {
        if (node == null || node.isNull()) {
            return List.of();
        }
        return modes(node);
    }

    private static List<ModWikiDocument.Multiplier> multipliers(JsonNode root)
    {
        JsonNode node = first(root, "score_multiplier", "multipliers");
        if (node == null || node.isNull()) {
            return List.of();
        }
        if (node.isTextual()) {
            int kind = root.path("multiplier_type").asInt(1);
            return List.of(new ModWikiDocument.Multiplier(List.of(), node.asText(), kind));
        }
        if (!node.isArray()) {
            throw new IllegalArgumentException("score_multiplier 格式无法识别");
        }
        List<ModWikiDocument.Multiplier> result = new ArrayList<>();
        for (JsonNode item : node) {
            if (item == null || item.isNull()) {
                continue;
            }
            if (item.isTextual()) {
                result.add(new ModWikiDocument.Multiplier(List.of(), item.asText(), 1));
                continue;
            }
            String text = firstText(item, "multiplier", "text");
            int kind = item.has("type") ? item.get("type").asInt(1) : item.path("multiplier_type").asInt(1);
            result.add(new ModWikiDocument.Multiplier(modes(item.get("mode")), text, kind));
        }
        return List.copyOf(result);
    }

    private static List<ModWikiDocument.Effect> effects(JsonNode node)
    {
        if (node == null || node.isNull() || !node.isArray()) {
            return List.of();
        }
        List<ModWikiDocument.Effect> result = new ArrayList<>();
        for (JsonNode item : node) {
            if (item == null || !item.isObject()) {
                continue;
            }
            result.add(new ModWikiDocument.Effect(
                    modes(item.get("mode")),
                    firstText(item, "description", "text")));
        }
        return List.copyOf(result);
    }

    private static List<ModWikiDocument.Shot> shots(JsonNode root)
    {
        JsonNode gameplay = root.get("gameplay");
        if (gameplay != null && gameplay.isObject()) {
            return List.of(shot(gameplay));
        }
        JsonNode shots = root.get("shots");
        if (shots == null || !shots.isArray()) {
            return List.of();
        }
        List<ModWikiDocument.Shot> result = new ArrayList<>();
        for (JsonNode item : shots) {
            if (item != null && item.isObject()) {
                result.add(shot(item));
            }
        }
        return List.copyOf(result);
    }

    private static ModWikiDocument.Shot shot(JsonNode node)
    {
        Integer width = node.hasNonNull("width") ? node.get("width").asInt() : null;
        Integer height = node.hasNonNull("height") ? node.get("height").asInt() : null;
        return new ModWikiDocument.Shot(
                firstText(node, "src", "href"),
                firstText(node, "description", "label"),
                width,
                height);
    }

    private static List<ModWikiDocument.ModRef> incompatible(JsonNode node)
    {
        if (node == null || node.isNull() || !node.isArray()) {
            return List.of();
        }
        List<ModWikiDocument.ModRef> result = new ArrayList<>();
        for (JsonNode item : node) {
            if (item == null || !item.isObject()) {
                continue;
            }
            String name = text(item, "name");
            if (name.isBlank() && item.hasNonNull("acronym")) {
                name = item.get("acronym").asText();
            }
            result.add(new ModWikiDocument.ModRef(text(item, "type"), name));
        }
        return List.copyOf(result);
    }

    private static String extra(JsonNode root)
    {
        if (root.has("additional_info")) {
            JsonNode node = root.get("additional_info");
            return node == null || node.isNull() ? null : node.asText();
        }
        if (root.has("extra")) {
            JsonNode node = root.get("extra");
            return node == null || node.isNull() ? null : node.asText();
        }
        return null;
    }

    private static List<String> modes(JsonNode node)
    {
        if (node == null || node.isNull()) {
            return List.of();
        }
        List<String> raw = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                raw.add(modeToken(item));
            }
        }
        else {
            raw.add(modeToken(node));
        }
        return ModWikiDocument.canonicalModes(raw);
    }

    private static String modeToken(JsonNode node)
    {
        if (node == null || node.isNull()) {
            return "";
        }
        if (node.isNumber()) {
            return Integer.toString(node.intValue());
        }
        return node.asText();
    }

    private static JsonNode first(JsonNode root, String primary, String fallback)
    {
        JsonNode node = root.get(primary);
        if (node != null && !node.isNull()) {
            return node;
        }
        return root.get(fallback);
    }

    private static String firstText(JsonNode root, String primary, String fallback)
    {
        String value = text(root, primary);
        return value.isBlank() ? text(root, fallback) : value;
    }

    private static String text(JsonNode root, String field)
    {
        if (root == null || !root.hasNonNull(field)) {
            return "";
        }
        return root.get(field).asText("");
    }
}
