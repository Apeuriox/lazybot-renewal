package me.aloic.lazybot.graphics.help;

import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Line positions for {@code single_command_help_svg.jte}.
 * The template owns the text elements. This class only wraps copy and
 * decides how far the 150px grid has to grow.
 */
public record CommandHelpPoster(
        String code,
        String headline,
        String title,
        String titleSize,
        String date,
        List<Line> descriptionLines,
        String descriptionRuleY,
        Block creators,
        Block designers,
        List<Parameter> parameters,
        Column examples,
        Column aliases,
        String availability,
        String availabilityFont,
        String footerColor,
        int height,
        int shift)
{
    public record Line(String y, String text, String fontFamily)
    {
    }

    public record Block(String labelY, String ruleY, List<Line> lines)
    {
        public boolean present()
        {
            return !lines.isEmpty();
        }
    }

    public record Parameter(String y, String heading, String headingFont, String flag, String ruleY, List<Line> lines)
    {
    }

    public record Column(String labelY, List<Line> lines)
    {
        public boolean present()
        {
            return !lines.isEmpty();
        }
    }

    private static final double BODY = 28;
    private static final double DESCRIPTION_WIDTH = 196;
    private static final double PARAMETER_WIDTH = 380;
    private static final double EXAMPLE_WIDTH = 560;
    private static final int BASE_HEIGHT = 2100;
    private static final double EXAMPLE_BOTTOM = 1912.09;
    private static final double FOOTER_TEXT_Y = 2043.09;
    private static final double EXAMPLE_STEP = 41;
    private static final double EXAMPLE_LABEL_GAP = 64;
    private static final double CONTENT_GAP = 48;
    private static final String FOOTER_READY = "#5E693E";
    private static final String FOOTER_INCOMPLETE = "#693E3E";
    private static final String CJK_FONT = "Noto Sans SC";

    public static CommandHelpPoster from(CommandHelp help)
    {
        String title = upper(help.getCommand());
        List<Line> descriptionLines = linesFrom(wrap(help.getDescription(), DESCRIPTION_WIDTH), 450.09, 33);
        String descriptionRuleY = descriptionLines.isEmpty()
                ? ""
                : num(450.09 + (descriptionLines.size() - 1L) * 33 + 30);
        double afterDescription = descriptionLines.isEmpty() ? 384.09 - 159 : parse(descriptionRuleY);

        Measured creators = block(afterDescription, 159, splitCsv(help.getCreator()));
        Measured designers = block(creators.end(), 147, splitCsv(help.getDesigner()));
        List<Parameter> parameters = parameters(help.getOptions());
        List<String> exampleCopy = wrapAll(help.getUsageExamples(), EXAMPLE_WIDTH);
        List<String> aliasCopy = numbered(splitCsv(help.getAlias()));
        double exampleTop = Math.min(columnTop(exampleCopy.size()), columnTop(aliasCopy.size()));
        double contentBottom = Math.max(parameterEnd(parameters), designers.end() + 36);
        double overflow = Math.max(0, contentBottom + CONTENT_GAP - exampleTop);
        int shift = (int) (Math.ceil(overflow / 150.0) * 150);

        String availability = trim(help.getAvailability());
        return new CommandHelpPoster(
                trim(help.getCode()),
                upper(help.getHeadline()),
                title,
                title.isEmpty() ? "" : num(titleSize(title)),
                formatDate(help.getInitialReleaseDate()),
                descriptionLines,
                descriptionRuleY,
                creators.view(),
                designers.view(),
                parameters,
                column(exampleCopy, EXAMPLE_BOTTOM + shift),
                column(aliasCopy, EXAMPLE_BOTTOM + shift),
                availability,
                fontFor(availability),
                CommandHelp.INCOMPLETE.equalsIgnoreCase(availability) ? FOOTER_INCOMPLETE : FOOTER_READY,
                BASE_HEIGHT + shift,
                shift);
    }

    private static Measured block(double previousRuleY, double labelGap, List<String> names)
    {
        if (names.isEmpty()) {
            return Measured.empty(previousRuleY);
        }
        double labelY = previousRuleY + labelGap;
        List<Line> lines = new ArrayList<>();
        double y = labelY + 42;
        for (int i = 0; i < names.size(); i++) {
            lines.add(line(y, "%02d  %s".formatted(i + 1, names.get(i))));
            y += 42;
        }
        double ruleY = y - 42 + 26;
        return new Measured(num(labelY), num(ruleY), List.copyOf(lines), ruleY);
    }

    private static List<Parameter> parameters(List<CommandParameter> options)
    {
        if (options == null || options.isEmpty()) {
            return List.of();
        }
        List<Parameter> rows = new ArrayList<>();
        double y = 450.09;
        int index = 1;
        for (CommandParameter option : options) {
            if (option == null) {
                continue;
            }
            String name = option.name() == null ? "" : option.name();
            String type = option.type() == null || option.type().isBlank() ? "" : " :" + option.type();
            String heading = "%02d  %s%s".formatted(index, name, type);
            List<Line> lines = linesFrom(wrap(option.description(), PARAMETER_WIDTH), y + 40, 33);
            double anchor = lines.isEmpty() ? y : parse(lines.getLast().y());
            double ruleY = anchor + (lines.isEmpty() ? 40 : 30);
            rows.add(new Parameter(
                    num(y),
                    heading,
                    fontFor(heading),
                    flag(option.optional()),
                    num(ruleY),
                    lines));
            y = ruleY + 94;
            index++;
        }
        return List.copyOf(rows);
    }

    private static double parameterEnd(List<Parameter> parameters)
    {
        if (parameters.isEmpty()) {
            return 0;
        }
        return parse(parameters.getLast().ruleY()) + 94;
    }

    private static Column column(List<String> rows, double bottom)
    {
        if (rows.isEmpty()) {
            return new Column("", List.of());
        }
        int count = rows.size();
        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            lines.add(line(bottom - (count - 1L - i) * EXAMPLE_STEP, rows.get(i)));
        }
        double top = bottom - (count - 1L) * EXAMPLE_STEP;
        return new Column(num(top - EXAMPLE_LABEL_GAP), List.copyOf(lines));
    }

    private static double columnTop(int rows)
    {
        if (rows <= 0) {
            return FOOTER_TEXT_Y - 36;
        }
        return EXAMPLE_BOTTOM - (rows - 1L) * EXAMPLE_STEP - EXAMPLE_LABEL_GAP;
    }

    private static List<Line> linesFrom(List<String> wrapped, double start, double step)
    {
        List<Line> lines = new ArrayList<>();
        double y = start;
        for (String text : wrapped) {
            lines.add(line(y, text));
            y += step;
        }
        return List.copyOf(lines);
    }

    private static List<String> wrapAll(List<String> values, double width)
    {
        List<String> lines = new ArrayList<>();
        if (values == null) {
            return lines;
        }
        for (String value : values) {
            lines.addAll(wrap(value, width));
        }
        return lines;
    }

    private static List<String> numbered(List<String> values)
    {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            lines.add("%02d  %s".formatted(i + 1, values.get(i).toUpperCase(Locale.ROOT)));
        }
        return lines;
    }

    private static String flag(CommandParameter.ParameterType type)
    {
        return type == CommandParameter.ParameterType.REQUIRED ? "REQUIRED" : "OPTIONAL";
    }

    private static double titleSize(String title)
    {
        int count = Math.max(title.codePointCount(0, title.length()), 1);
        if (count <= 12) {
            return 200;
        }
        return Math.max(72, 200.0 * 12.0 / count);
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

    private static List<String> wrap(String text, double maxWidth)
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
        }
        return lines;
    }

    private static Line line(double y, String text)
    {
        return new Line(num(y), text, fontFor(text));
    }

    private static String fontFor(String content)
    {
        if (content == null || content.isBlank()) {
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

    private static String trim(String raw)
    {
        return raw == null ? "" : raw.trim();
    }

    private static String upper(String raw)
    {
        String value = trim(raw);
        return value.isEmpty() ? "" : value.toUpperCase(Locale.ROOT);
    }

    private static String num(double value)
    {
        return String.format(Locale.ROOT, "%.3f", value);
    }

    private static double parse(String value)
    {
        return Double.parseDouble(value);
    }

    private record Measured(String labelY, String ruleY, List<Line> lines, double end)
    {
        private static Measured empty(double end)
        {
            return new Measured("", "", List.of(), end);
        }

        private Block view()
        {
            return new Block(labelY, ruleY, lines);
        }
    }
}
