package me.aloic.lazybot.graphics.help;

import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Text positions for {@code single_command_help_svg.jte}.
 * The 1500×2100 poster keeps the Figma anchors. Description, credits and
 * parameters stack downward; examples stay pinned above the footer.
 */
public record CommandHelpPoster(List<PlacedText> underHalftone, List<PlacedText> overHalftone)
{
    public record PlacedText(
            String x,
            String y,
            String text,
            String fill,
            String fontFamily,
            String fontSize,
            String letterSpacing)
    {
    }

    private static final double BODY = 28;
    private static final double DESCRIPTION_WIDTH = 196;
    private static final double PARAMETER_WIDTH = 380;
    private static final double EXAMPLE_WIDTH = 560;

    public static CommandHelpPoster from(CommandHelp help)
    {
        List<PlacedText> under = new ArrayList<>();
        List<PlacedText> over = new ArrayList<>();
        placeTitle(under, help);
        double ruleY = placeDescription(under, help.getDescription());
        ruleY = placePeople(under, ruleY, 159, "\u201cCREATOR\u201d", splitCsv(help.getCreator()));
        placePeople(under, ruleY, 147, "\u201cGRAPHIC_DESIGNER\u201d", splitCsv(help.getDesigner()));
        placeParameters(over, help.getOptions());
        placeExamples(over, help);
        return new CommandHelpPoster(List.copyOf(under), List.copyOf(over));
    }

    private static void placeTitle(List<PlacedText> lines, CommandHelp help)
    {
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
        lines.add(body(101, 384.09, "\u201cDESCRIPTION\u201d"));
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
            lines.add(body(1316, y, flag(option.optional())));
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
        if (!examples.isEmpty()) {
            lines.add(body(356, 1766.09, "(EXAMPLE)"));
            double y = 1830.09;
            for (String example : examples) {
                for (String line : wrap(example, EXAMPLE_WIDTH, BODY)) {
                    lines.add(body(356, y, line));
                    y += 41;
                }
            }
        }
        if (!aliases.isEmpty()) {
            lines.add(body(954, 1755.09, "\u201cINVOKE\u201d"));
            double y = 1824.09;
            for (int i = 0; i < aliases.size(); i++) {
                lines.add(body(959, y, "%02d  %s".formatted(i + 1, aliases.get(i))));
                y += 41;
            }
        }
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
        return text(x, y, content, "#3A3C2E", "Rubik", BODY, "0em");
    }

    private static PlacedText rule(double x, double y)
    {
        return text(x, y, "_", "#3A3C2E", "Rubik", BODY, "-0.02em");
    }

    private static PlacedText text(double x, double y, String content, String fill,
                                   String family, double size, String spacing)
    {
        return new PlacedText(num(x), num(y), content, fill, family, num(size), spacing);
    }

    private static String num(double value)
    {
        return String.format(Locale.ROOT, "%.3f", value);
    }
}
