package me.aloic.lazybot.graphics.help;

import me.aloic.lazybot.entity.CommandSummary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Row positions for {@code help_index_svg.jte}.
 * The page height stays on the 200px grid. Section panels grow by the exact
 * text overflow, because those rectangles are not on the grid.
 */
public record HelpIndexPoster(
        List<Fragment> osuRows,
        List<Fragment> customizeRows,
        List<Fragment> preferenceRows,
        List<Fragment> funRows,
        List<Fragment> otherRows,
        int height,
        double osuGrowth,
        double customizeGrowth,
        double preferenceGrowth,
        double funGrowth,
        double contentShift)
{
    public record Fragment(String x, String y, String text, String fontFamily, String size, String opacity)
    {
    }

    private static final int BASE_HEIGHT = 4600;
    private static final int GRID = 200;
    private static final double OSU_START = 1191.41;
    private static final double OSU_LIMIT = 2240;
    private static final double MIDDLE_LIMIT = 4001;
    private static final double BODY = 24;
    private static final double LINE = 31;
    private static final double ROW_GAP = 42;
    private static final String CJK_FONT = "Noto Sans SC";

    public static HelpIndexPoster from(List<CommandSummary> summaries)
    {
        List<CommandSummary> source = summaries == null ? List.of() : summaries;
        List<Fragment> osu = new ArrayList<>();
        double osuBottom = layTable(osu, of(source, CommandSummary.Category.OSU), OSU_START, true);
        double osuGrowth = Math.max(0, osuBottom - OSU_LIMIT);

        List<Fragment> customize = new ArrayList<>();
        List<Fragment> preference = new ArrayList<>();
        List<Fragment> fun = new ArrayList<>();
        List<Fragment> others = new ArrayList<>();
        double customizeBottom = layCustomize(customize, of(source, CommandSummary.Category.CUSTOMIZE));
        double preferenceBottom = layPreference(preference, of(source, CommandSummary.Category.PREFERENCE));
        double funBottom = layTable(fun, of(source, CommandSummary.Category.FUN), 3298.41, false);
        double otherStart = Math.max(3520, funBottom + 80);
        double otherBottom = layTable(others, of(source, CommandSummary.Category.OTHERS), otherStart, true);
        double middleBottom = Math.max(customizeBottom, Math.max(preferenceBottom, Math.max(funBottom, otherBottom)));
        double middleGrowth = Math.max(0, middleBottom - MIDDLE_LIMIT);
        double contentShift = osuGrowth + middleGrowth;
        return new HelpIndexPoster(List.copyOf(osu), List.copyOf(customize), List.copyOf(preference),
                List.copyOf(fun), List.copyOf(others), BASE_HEIGHT + snap(contentShift), osuGrowth,
                Math.max(0, customizeBottom - 2989),
                Math.max(0, preferenceBottom - 2989),
                Math.max(0, funBottom - 3463),
                contentShift);
    }

    private static List<CommandSummary> of(List<CommandSummary> summaries, CommandSummary.Category category)
    {
        return summaries.stream()
                .filter(summary -> summary.category() == category)
                .sorted(Comparator.comparing(summary -> summary.command() == null
                        ? ""
                        : summary.command().toLowerCase(Locale.ROOT)))
                .toList();
    }

    private static double layTable(List<Fragment> out, List<CommandSummary> rows, double start, boolean wide)
    {
        double y = start;
        for (CommandSummary row : rows) {
            List<String> command = wrap(shown(row.command(), "\\"), wide ? 126 : 210);
            List<String> alias = wide ? wrap(aliases(row), 130) : List.of();
            List<String> parameter = wrap(shown(row.parameter(), "\\"), wide ? 250 : 280);
            List<String> example = wrap(shown(row.example(), "\\"), wide ? 210 : 280);
            List<String> description = wrap(shown(row.description(), ""), wide ? 226 : 760);
            List<String> caution = wide ? wrap(shown(row.caution(), "\\"), 200) : List.of();
            int lines = Math.max(command.size(), Math.max(parameter.size(),
                    Math.max(example.size(), Math.max(description.size(), Math.max(alias.size(), caution.size())))));
            addColumn(out, command, wide ? 214 : 212, y);
            if (wide) {
                addColumn(out, alias, 414, y);
                addColumn(out, parameter, 658, y);
                addColumn(out, example, 1000, y);
                addColumn(out, description, 1330, y);
                addColumn(out, caution, 1645, y);
            }
            else {
                addColumn(out, parameter, 446, y);
                addColumn(out, example, 758, y);
                addColumn(out, description, 1073, y);
            }
            y += Math.max(0, lines - 1) * LINE + ROW_GAP;
        }
        return rows.isEmpty() ? start : y;
    }

    private static double layCustomize(List<Fragment> out, List<CommandSummary> rows)
    {
        double y = 2618.41;
        for (CommandSummary row : rows) {
            String title = row.subcommand() == null || row.subcommand().isBlank()
                    ? shown(row.command(), "")
                    : row.subcommand() + " " + shown(row.parameter(), "");
            add(out, 116, y, title.trim(), "1");
            y += 66;
            for (String line : wrap(shown(row.description(), ""), 860)) {
                add(out, 116, y, line, "0.7");
                y += LINE;
            }
            y += 11;
            add(out, 116, y, shown(row.example(), ""), "0.9");
            y += 76;
        }
        return rows.isEmpty() ? y : y;
    }

    private static double layPreference(List<Fragment> out, List<CommandSummary> rows)
    {
        if (rows.isEmpty()) {
            return 2541;
        }
        double[] columns = {1122, 1411, 1703};
        double bottom = 2541;
        for (int offset = 0; offset < rows.size(); offset += columns.length) {
            double rowBottom = 2541;
            int count = Math.min(columns.length, rows.size() - offset);
            for (int column = 0; column < count; column++) {
                CommandSummary row = rows.get(offset + column);
                double x = columns[column];
                double y = 2541 + (offset / columns.length) * 620.0;
                add(out, x, y, shown(row.command(), ""), "1");
                y += 85;
                for (String line : wrap(shown(row.description(), ""), 250)) {
                    add(out, x, y, line, "1");
                    y += 28;
                }
                y += 24;
                for (String line : wrap(shown(row.parameter(), ""), 250)) {
                    add(out, x, y, line, "1");
                    y += 28;
                }
                y += 36;
                add(out, x, y, shown(row.example(), ""), "1");
                rowBottom = Math.max(rowBottom, y + 40);
            }
            bottom = rowBottom;
        }
        return bottom;
    }

    private static void addColumn(List<Fragment> out, List<String> lines, double x, double start)
    {
        double y = start;
        for (String line : lines) {
            add(out, x, y, line, "1");
            y += LINE;
        }
    }

    private static void add(List<Fragment> out, double x, double y, String text, String opacity)
    {
        if (text == null || text.isBlank()) {
            return;
        }
        out.add(new Fragment(num(x), num(y), text, fontFor(text), num(BODY), opacity));
    }

    private static String aliases(CommandSummary row)
    {
        if (row.alias() == null || row.alias().isEmpty()) {
            return "\\";
        }
        return String.join(",", row.alias());
    }

    private static String shown(String value, String fallback)
    {
        if (value == null || value.isBlank() || "\\".equals(value) || "/".equals(value)) {
            return fallback;
        }
        return value.trim();
    }

    private static int snap(double overflow)
    {
        if (overflow <= 0) {
            return 0;
        }
        return (int) (Math.ceil(overflow / GRID) * GRID);
    }

    private static List<String> wrap(String text, double maxWidth)
    {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        double width = 0;
        int index = 0;
        while (index < text.length()) {
            int codePoint = text.codePointAt(index);
            int next = index + Character.charCount(codePoint);
            double charWidth = codePoint > 0xFF ? BODY : BODY * 0.56;
            if (!current.isEmpty() && width + charWidth > maxWidth) {
                lines.add(current.toString());
                current.setLength(0);
                width = 0;
                if (codePoint == ' ') {
                    index = next;
                    continue;
                }
            }
            current.appendCodePoint(codePoint);
            width += charWidth;
            index = next;
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    private static String fontFor(String content)
    {
        return content.codePoints().anyMatch(HelpIndexPoster::isCjk) ? CJK_FONT : "Rubik";
    }

    private static boolean isCjk(int codePoint)
    {
        Character.UnicodeScript script = Character.UnicodeScript.of(codePoint);
        return script == Character.UnicodeScript.HAN
                || script == Character.UnicodeScript.HIRAGANA
                || script == Character.UnicodeScript.KATAKANA
                || script == Character.UnicodeScript.HANGUL;
    }

    private static String num(double value)
    {
        return String.format(Locale.ROOT, "%.3f", value);
    }
}
