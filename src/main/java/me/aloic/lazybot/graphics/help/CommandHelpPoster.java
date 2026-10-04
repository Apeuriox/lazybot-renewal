package me.aloic.lazybot.graphics.help;

import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Text positions for {@code single_command_help_svg.jte}.
 * Description, credits and parameters stack downward. Examples and invoke
 * names are anchored above the footer and grow upward.
 */
public record CommandHelpPoster(
        List<PlacedText> underHalftone,
        List<PlacedText> overHalftone,
        String footerColor,
        String availability)
{
    public record PlacedText(
            String x,
            String y,
            String text,
            String fill,
            String fontFamily,
            String fontSize,
            String letterSpacing,
            String anchor)
    {
    }

    private static final double BODY = 28;
    private static final double DESCRIPTION_WIDTH = 196;
    private static final double PARAMETER_WIDTH = 380;
    private static final double EXAMPLE_WIDTH = 560;
    private static final double EXAMPLE_BOTTOM = 1912.09;
    private static final double EXAMPLE_STEP = 41;
    private static final double EXAMPLE_LABEL_GAP = 64;
    private static final String FOOTER_READY = "#5E693E";
    private static final String FOOTER_INCOMPLETE = "#693E3E";
    private static final double RIGHT_EDGE = 1436;
    private static final String CJK_FONT = "Noto Sans SC";

    public static CommandHelpPoster from(CommandHelp help)
    {
        List<PlacedText> under = new ArrayList<>();
        List<PlacedText> over = new ArrayList<>();
        placeTitle(under, help);
        double ruleY = placeDescription(under, help.getDescription());
        ruleY = placePeople(under, ruleY, 159, "\"CREATOR\"", splitCsv(help.getCreator()));
        placePeople(under, ruleY, 147, "\"GRAPHIC_DESIGNER\"", splitCsv(help.getDesigner()));
        placeParameters(over, help.getOptions());
        placeExamples(over, help);
        String availability = help.getAvailability() == null ? "" : help.getAvailability().trim();
        if (!availability.isEmpty()) {
            over.add(text(RIGHT_EDGE, 2043.09, availability, "black", fontFor(availability), BODY, "0em", "end"));
        }
        String footerColor = CommandHelp.INCOMPLETE.equalsIgnoreCase(availability)
                ? FOOTER_INCOMPLETE
                : FOOTER_READY;
        return new CommandHelpPoster(List.copyOf(under), List.copyOf(over), footerColor, availability);
    }

    private static void placeTitle(List<PlacedText> lines, CommandHelp help)
    {
        String code = help.getCode() == null ? "" : help.getCode().trim();
        if (!code.isEmpty()) {
            lines.add(text(102, 95.535, code, "#3A3C2E", "Rubik", 22, "0em"));
        }
        String headline = help.getHeadline() == null ? "" : help.getHeadline().trim().toUpperCase();
        if (!headline.isEmpty()) {
            lines.add(text(439, 94.535, headline, "#3A3C2E", "Rubik", 22, "0em"));
        }
        String command = help.getCommand() == null ? "" : help.getCommand().trim();
        if (!command.isEmpty()) {
            String title = command.toLowerCase(Locale.ROOT);
            lines.add(text(81, 300.406, title, "#0C0F00", "Monorama", titleSize(title), "-0.02em"));
        }
        String date = formatDate(help.getInitialReleaseDate());
        if (!date.isEmpty()) {
            lines.add(text(936, 95.535, date, "#3A3C2E", "Rubik", 22, "0em"));
        }
    }

    private static double placeDescription(List<PlacedText> lines, String description)
    {
        List<String> wrapped = wrap(description, DESCRIPTION_WIDTH, BODY);
        if (wrapped.isEmpty()) {
            return 384.09 - 159;
        }
        lines.add(body(101, 384.09, "\"DESCRIPTION\""));
        double y = 450.09;
        for (String line : wrapped) {
            lines.add(body(101, y, line));
            y += 33;
        }
        double ruleY = y - 33 + 30;
        lines.add(rule(101, ruleY));
        return ruleY;
    }

    private static double placePeople(List<PlacedText> lines, double previousRuleY, double labelGap,
                                      String label, List<String> names)
    {
        if (names.isEmpty()) {
            return previousRuleY;
        }
        double labelY = previousRuleY + labelGap;
        lines.add(body(101, labelY, label));
        double y = labelY + 42;
        for (int i = 0; i < names.size(); i++) {
            lines.add(body(101, y, "%02d  %s".formatted(i + 1, names.get(i))));
            y += 42;
        }
        double ruleY = y - 42 + 26;
        lines.add(rule(101, ruleY));
        return ruleY;
    }

    private static void placeParameters(List<PlacedText> lines, List<CommandParameter> options)
    {
        if (options == null || options.isEmpty()) {
            return;
        }
        lines.add(body(525, 384.09, "(PARAMETERS)"));
        double y = 450.09;
        int index = 1;
        for (CommandParameter option : options) {
            if (option == null) {
                continue;
            }
            String name = option.name() == null ? "" : option.name();
            String valueType = option.type() == null || option.type().isBlank() ? "" : " :" + option.type();
            lines.add(body(525, y, "%02d  %s%s".formatted(index, name, valueType)));
            lines.add(endAligned(RIGHT_EDGE, y, flag(option.optional())));
            List<String> wrapped = wrap(option.description(), PARAMETER_WIDTH, BODY);
            double lineY = y + 40;
            for (String line : wrapped) {
                lines.add(body(525, lineY, line));
                lineY += 33;
            }
            double anchor = wrapped.isEmpty() ? y : lineY - 33;
            double ruleY = anchor + (wrapped.isEmpty() ? 40 : 30);
            lines.add(rule(525, ruleY));
            y = ruleY + 94;
            index++;
        }
    }

    private static void placeExamples(List<PlacedText> lines, CommandHelp help)
    {
        List<String> examples = help.getUsageExamples() == null ? List.of() : help.getUsageExamples();
        List<String> aliases = splitCsv(help.getAlias()).stream()
                .map(alias -> alias.toUpperCase(Locale.ROOT))
                .toList();
        List<String> exampleLines = new ArrayList<>();
        for (String example : examples) {
            exampleLines.addAll(wrap(example, EXAMPLE_WIDTH, BODY));
        }
        placeUpward(lines, 356, exampleLines, 356, "(EXAMPLE)");

        List<String> aliasLines = new ArrayList<>();
        for (int i = 0; i < aliases.size(); i++) {
            aliasLines.add("%02d  %s".formatted(i + 1, aliases.get(i)));
        }
        placeUpward(lines, 959, aliasLines, 954, "\"INVOKE\"");
    }

    private static void placeUpward(List<PlacedText> lines, double textX, List<String> rows,
                                    double labelX, String label)
    {
        if (rows.isEmpty()) {
            return;
        }
        int count = rows.size();
        for (int i = 0; i < count; i++) {
            double y = EXAMPLE_BOTTOM - (count - 1L - i) * EXAMPLE_STEP;
            lines.add(body(textX, y, rows.get(i)));
        }
        double top = EXAMPLE_BOTTOM - (count - 1L) * EXAMPLE_STEP;
        lines.add(body(labelX, top - EXAMPLE_LABEL_GAP, label));
    }

    private static String flag(CommandParameter.ParameterType type)
    {
        return type == CommandParameter.ParameterType.REQUIRED ? "REQUIRED" : "OPTIONAL";
    }

    private static double titleSize(String title)
    {
        int count = Math.max(title.codePointCount(0, title.length()), 1);
        if (count <= 8) {
            return 200;
        }
        return Math.max(72, 200.0 * 8 / count);
    }

    private static String formatDate(String raw)
    {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        return raw.trim().replace('-', '.');
    }

    private static List<String> splitCsv(String raw)
    {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split("[,，]"))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .toList();
    }

    private static List<String> wrap(String text, double maxWidth, double fontSize)
    {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<String> lines = new ArrayList<>();
        for (String paragraph : text.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)) {
            if (paragraph.isEmpty()) {
                continue;
            }
            StringBuilder current = new StringBuilder();
            double width = 0;
            int index = 0;
            while (index < paragraph.length()) {
                int codePoint = paragraph.codePointAt(index);
                int next = index + Character.charCount(codePoint);
                double charWidth = codePoint > 0xFF ? fontSize : fontSize * 0.56;
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
        }
        return lines;
    }

    private static PlacedText body(double x, double y, String content)
    {
        return text(x, y, content, "#3A3C2E", fontFor(content), BODY, "0em", "start");
    }

    private static PlacedText endAligned(double x, double y, String content)
    {
        return text(x, y, content, "#3A3C2E", fontFor(content), BODY, "0em", "end");
    }

    private static PlacedText rule(double x, double y)
    {
        return text(x, y, "_", "#3A3C2E", "Rubik", BODY, "-0.02em", "start");
    }

    private static PlacedText text(double x, double y, String content, String fill,
                                   String family, double size, String spacing)
    {
        return text(x, y, content, fill, family, size, spacing, "start");
    }

    private static PlacedText text(double x, double y, String content, String fill,
                                   String family, double size, String spacing, String anchor)
    {
        return new PlacedText(num(x), num(y), content, fill, family, num(size), spacing, anchor);
    }

    private static String fontFor(String content)
    {
        if (content == null) {
            return "Rubik";
        }
        return content.codePoints().anyMatch(CommandHelpPoster::isCjk) ? CJK_FONT : "Rubik";
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
