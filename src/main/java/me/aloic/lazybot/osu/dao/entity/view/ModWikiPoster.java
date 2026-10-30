package me.aloic.lazybot.osu.dao.entity.view;

import me.aloic.lazybot.osu.wiki.ModWikiDocument;
import me.aloic.lazybot.osu.wiki.ModWikiIcon;
import me.aloic.lazybot.osu.wiki.ModWikiModes;
import me.aloic.lazybot.osu.wiki.ModWikiType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Line positions for {@code mod_wiki_svg.jte}.
 * Coordinates follow {@code ModWiki.svg}. The template owns the shapes.
 */
public record ModWikiPoster(
        int height,
        String title,
        String titleSize,
        String titleFont,
        Badge badge,
        List<PlacedMode> headerModes,
        String iconColor,
        String iconTransform,
        String iconAdjust,
        String iconMarkup,
        String acronym,
        String abbrevY,
        String abbrevValueX,
        String typeY,
        String typeValueX,
        String typeValue,
        String typeColor,
        String typeFont,
        String multiplierY,
        List<MultiplierLine> multiplierLines,
        String hotkeyY,
        String hotkeyX,
        String hotkey,
        String hotkeyFont,
        String effectsY,
        List<EffectBlock> effects,
        String shotsY,
        List<ShotSlot> shots,
        String incompatibleY,
        List<Pill> pills,
        String extraY,
        String extraX,
        List<Line> extraLines,
        UnrankedNote unranked,
        Chip hotkeyChip)
{
    public record Badge(String transform, String ranked, String partial, String unranked, String left, String width)
    {
    }

    public record PlacedMode(String id, String transform)
    {
    }

    public record Span(String x, String text, String fill, String font)
    {
    }

    public record MultiplierLine(String y, List<PlacedMode> icons, String textX, boolean lead, List<Span> spans)
    {
        public boolean iconPresent()
        {
            return icons != null && !icons.isEmpty();
        }
    }

    public record EffectBlock(List<PlacedMode> icons, String textX, String bodyX, String colonY, List<Line> lines)
    {
    }

    public record Line(String x, String y, String text, String font, boolean lead)
    {
    }

    public record UnrankedNote(boolean present, String openX, String closeX, List<PlacedMode> icons)
    {
        private static UnrankedNote hidden()
        {
            return new UnrankedNote(false, "0", "0", List.of());
        }
    }

    public record Chip(String x, String y, String width, String height)
    {
    }

    public record ShotSlot(
            String x,
            String y,
            String width,
            String height,
            String href,
            boolean captionPresent,
            String captionX,
            String captionY,
            String caption,
            String captionFont)
    {
    }

    public record Pill(
            String x,
            String y,
            String width,
            String textX,
            String textY,
            String text,
            String font,
            String fill)
    {
    }

    private static final double LABEL_X = 53;
    private static final double ROW = 34;
    private static final double CANVAS_W = 1000;
    private static final double RIGHT = 28;
    private static final double SHOT_X = 165;
    private static final double PILL_X = 215;
    private static final double SHOT_MAX_W = 711;
    private static final double SLOT_X = 104;
    private static final double SLOT_Y = 122;
    private static final double SLOT_W = 58;
    private static final double SLOT_H = 41;
    private static final String ABBREV = "·  缩写: ";
    private static final String TYPE = "·  类型: ";
    private static final String MULTIPLIER = "·  分数倍率: ";
    private static final String HOTKEY = "·  默认快捷键: ";
    private static final String EFFECTS = "·  具体作用:";
    private static final String SHOTS = "·  实际画面:";
    private static final String INCOMPATIBLE = "·  不兼容的Mod:";
    private static final String EXTRA = "·  额外信息: ";
    private static final String CJK = "Noto Sans SC";
    private static final String LATIN = "Rubik";
    private static final String LABEL = "#DAD4ED";

    public static ModWikiPoster from(ModWikiDocument page)
    {
        ModWikiIcon shape = page.icon() != null && page.icon().present() ? page.icon() : null;
        ModWikiType type = ModWikiType.fromLabel(page.type());
        List<String> modes = ModWikiModes.ordered(page.modes());
        List<String> unrankedModes = ModWikiModes.ordered(page.unrankedModes());
        TitleFit title = fitTitle(pageTitle(page), page.rank(), modesLeft(modes), unrankedWidth(unrankedModes));
        List<PlacedMode> header = headerModes(modes);
        UnrankedNote unranked = unrankedNote(unrankedModes, baseline(title.badge().left()) + baseline(title.badge().width()));

        double ink = 172;
        double cursor = 194.85;
        String abbrevY = num(cursor);
        ink = Math.max(ink, cursor + 12);
        cursor += ROW;
        String typeY = num(cursor);
        ink = Math.max(ink, cursor + 12);
        cursor += ROW;

        List<MultiplierLine> multiplierLines = multiplierLines(page.multipliers(), cursor);
        String multiplierY = multiplierLines.isEmpty() ? "0" : multiplierLines.getFirst().y();
        if (!multiplierLines.isEmpty()) {
            cursor = baseline(multiplierLines.getLast().y()) + ROW;
            ink = Math.max(ink, baseline(multiplierLines.getLast().y()) + 12);
        }

        String hotkeyY = num(cursor);
        Chip hotkeyChip = hotkeyChip(page.hotkey(), cursor);
        ink = Math.max(ink, cursor + 12);
        cursor += ROW;

        List<EffectBlock> effects = effectBlocks(page.effects(), cursor);
        String effectsY = effects.isEmpty() ? "0" : num(cursor);
        if (!effects.isEmpty()) {
            double last = baseline(effects.getLast().lines().getLast().y());
            ink = Math.max(ink, last + 10);
            cursor = last + 36;
        }

        List<ShotSlot> shots = shotSlots(page.shots(), cursor);
        String shotsY = shots.isEmpty() ? "0" : num(cursor);
        if (!shots.isEmpty()) {
            ShotSlot last = shots.getLast();
            double shotBottom = baseline(last.y()) + baseline(last.height());
            if (last.captionPresent()) {
                ink = Math.max(ink, baseline(last.captionY()) + 10);
                cursor = baseline(last.captionY()) + 48;
            }
            else {
                ink = Math.max(ink, shotBottom);
                cursor = shotBottom + 40;
            }
        }

        List<Pill> pills = pills(page.incompatible(), cursor);
        String incompatibleY = num(cursor);
        if (pills.isEmpty()) {
            ink = Math.max(ink, cursor + 12);
            cursor += ROW;
        }
        else {
            double pillBottom = 0;
            for (Pill pill : pills) {
                pillBottom = Math.max(pillBottom, baseline(pill.y()) + 26);
            }
            ink = Math.max(ink, pillBottom);
            cursor = pillBottom + 28.85;
        }

        String extraX = num(valueX(EXTRA));
        List<Line> extraLines = extraLines(page.extra(), cursor, baseline(extraX));
        String extraY = extraLines.isEmpty() ? "0" : num(cursor);
        if (!extraLines.isEmpty()) {
            ink = Math.max(ink, baseline(extraLines.getLast().y()) + 12);
        }

        int height = (int) Math.ceil(Math.max(ink + 24, 220));
        return new ModWikiPoster(
                height,
                title.text(),
                num(title.size()),
                "Space Mono, Noto Sans SC",
                title.badge(),
                header,
                type.icon(),
                shape == null ? "translate(0 0)" : iconTransform(shape),
                shape == null ? "translate(0 0)" : shape.transform(),
                shape == null ? "" : shape.markup(),
                page.acronym(),
                abbrevY,
                num(valueX(ABBREV)),
                typeY,
                num(valueX(TYPE)),
                page.type(),
                type.text(),
                fontFor(page.type()),
                multiplierY,
                multiplierLines,
                hotkeyY,
                num(hotkeyValueX()),
                page.hotkey(),
                fontFor(page.hotkey()),
                effectsY,
                effects,
                shotsY,
                shots,
                incompatibleY,
                pills,
                extraY,
                extraX,
                extraLines,
                unranked,
                hotkeyChip);
    }

    public String labelX()
    {
        return num(LABEL_X);
    }

    public String abbrevLabel()
    {
        return ABBREV;
    }

    public String typeLabel()
    {
        return TYPE;
    }

    public String multiplierLabel()
    {
        return MULTIPLIER;
    }

    public String hotkeyLabel()
    {
        return HOTKEY;
    }

    public String effectsLabel()
    {
        return EFFECTS;
    }

    public String shotsLabel()
    {
        return SHOTS;
    }

    public String incompatibleLabel()
    {
        return INCOMPATIBLE;
    }

    public String extraLabel()
    {
        return EXTRA;
    }

    public String incompatibleNoneX()
    {
        return num(valueX(INCOMPATIBLE) + 10);
    }

    public boolean iconPresent()
    {
        return iconMarkup != null && !iconMarkup.isBlank();
    }

    public boolean multipliersPresent()
    {
        return !multiplierLines.isEmpty();
    }

    public boolean effectsPresent()
    {
        return !effects.isEmpty();
    }

    public boolean shotsPresent()
    {
        return !shots.isEmpty();
    }

    public boolean incompatiblePresent()
    {
        return !pills.isEmpty();
    }

    public boolean extraPresent()
    {
        return !extraLines.isEmpty();
    }

    private static List<PlacedMode> headerModes(List<String> modes)
    {
        int count = modes.size();
        List<PlacedMode> placed = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String mode = modes.get(i);
            double target = 950 - (count - 1L - i) * 56;
            double dx = target - ModWikiModes.originX(mode);
            placed.add(new PlacedMode(
                    ModWikiModes.symbolId(mode),
                    String.format(Locale.ROOT, "translate(%.3f 0)", dx)));
        }
        return List.copyOf(placed);
    }

    private static List<MultiplierLine> multiplierLines(List<ModWikiDocument.Multiplier> multipliers, double start)
    {
        List<ModWikiDocument.Multiplier> usable = new ArrayList<>();
        boolean byMode = false;
        for (ModWikiDocument.Multiplier multiplier : multipliers) {
            if (multiplier.text() == null || multiplier.text().isBlank()) {
                continue;
            }
            usable.add(multiplier);
            if (!multiplier.modes().isEmpty()) {
                byMode = true;
            }
        }
        if (usable.isEmpty()) {
            return List.of();
        }
        if (usable.size() > 1 && byMode) {
            return modeMultipliers(usable, start);
        }
        return inlineMultipliers(usable, start);
    }

    private static List<MultiplierLine> modeMultipliers(List<ModWikiDocument.Multiplier> multipliers, double start)
    {
        List<MultiplierLine> lines = new ArrayList<>();
        double y = start;
        for (ModWikiDocument.Multiplier multiplier : multipliers) {
            IconRow icons = iconRow(multiplier.modes(), valueX(MULTIPLIER) + 12, y - 6, 0.393, 22, 20);
            lines.add(new MultiplierLine(
                    num(y),
                    icons.modes(),
                    num(icons.textX()),
                    lines.isEmpty(),
                    List.of(new Span(
                            "",
                            multiplier.text().trim(),
                            ModWikiType.multiplierColor(multiplier.kind()),
                            fontFor(multiplier.text())))));
            y += 32;
        }
        return List.copyOf(lines);
    }

    private static List<MultiplierLine> inlineMultipliers(List<ModWikiDocument.Multiplier> multipliers, double start)
    {
        List<Span> spans = new ArrayList<>();
        for (int i = 0; i < multipliers.size(); i++) {
            if (i > 0) {
                spans.add(new Span("", " / ", LABEL, LATIN));
            }
            String text = multipliers.get(i).text().trim();
            spans.add(new Span(
                    "",
                    text,
                    ModWikiType.multiplierColor(multipliers.get(i).kind()),
                    fontFor(text)));
        }
        return List.of(new MultiplierLine(num(start), List.of(), "", true, List.copyOf(spans)));
    }

    private static List<EffectBlock> effectBlocks(List<ModWikiDocument.Effect> effects, double headerY)
    {
        List<EffectBlock> blocks = new ArrayList<>();
        double lineY = headerY + 26;
        for (ModWikiDocument.Effect effect : effects) {
            if (effect.text() == null || effect.text().isBlank()) {
                continue;
            }
            IconRow icons = iconRow(effect.modes(), 96, lineY - 6, 0.393, 22, 18);
            double textX = icons.modes().isEmpty() ? 109 : icons.textX();
            double bodyX = icons.modes().isEmpty() ? 121 : icons.bodyX();
            List<String> wrapped = wrap(effect.text(), 18, CANVAS_W - bodyX - RIGHT);
            if (wrapped.isEmpty()) {
                continue;
            }
            List<Line> lines = new ArrayList<>();
            double y = lineY;
            boolean lead = true;
            for (String text : wrapped) {
                lines.add(new Line(lead ? "" : num(bodyX), num(y), text, fontFor(text), lead));
                lead = false;
                y += 28;
            }
            blocks.add(new EffectBlock(
                    icons.modes(),
                    num(textX),
                    num(bodyX),
                    num(lineY),
                    List.copyOf(lines)));
            lineY = y - 28 + 34;
        }
        return List.copyOf(blocks);
    }

    private static List<ShotSlot> shotSlots(List<ModWikiDocument.Shot> shots, double headerY)
    {
        if (shots.isEmpty()) {
            return List.of();
        }
        List<ShotSlot> slots = new ArrayList<>();
        double top = headerY + 14;
        for (ModWikiDocument.Shot shot : shots) {
            int rawWidth = shot.width() == null ? 711 : shot.width();
            int rawHeight = shot.height() == null ? 202 : shot.height();
            double width = Math.max(rawWidth, 1);
            double height = Math.max(rawHeight, 1);
            if (width > SHOT_MAX_W) {
                height = height * SHOT_MAX_W / width;
                width = SHOT_MAX_W;
            }
            String label = shot.label() == null ? "" : shot.label().trim();
            boolean caption = !label.isBlank();
            double captionY = top + height + 29;
            slots.add(new ShotSlot(
                    num(SHOT_X),
                    num(top),
                    num(width),
                    num(height),
                    shot.src(),
                    caption,
                    num(SHOT_X + width / 2),
                    num(captionY),
                    label,
                    fontFor(label)));
            top = caption ? captionY + 18 : top + height + 16;
        }
        return List.copyOf(slots);
    }

    private static List<Pill> pills(List<ModWikiDocument.ModRef> refs, double labelY)
    {
        if (refs == null || refs.isEmpty()) {
            return List.of();
        }
        double x = PILL_X;
        double y = labelY - 20.85;
        double limit = CANVAS_W - RIGHT;
        List<Pill> pills = new ArrayList<>();
        for (ModWikiDocument.ModRef ref : refs) {
            String text = ref.name() == null ? "" : ref.name().trim();
            if (text.isBlank()) {
                continue;
            }
            double width = monoWidth(text, 18) + 20;
            if (!pills.isEmpty() && x + width > limit) {
                x = PILL_X;
                y += 34;
            }
            pills.add(new Pill(
                    num(x),
                    num(y),
                    num(width),
                    num(x + 11),
                    num(y + 19.665),
                    text,
                    "Space Mono, Noto Sans SC",
                    ModWikiType.fromKey(ref.type()).pill()));
            x += width + 10;
        }
        return List.copyOf(pills);
    }

    private static List<Line> extraLines(String extra, double start, double x)
    {
        List<String> wrapped = wrap(extra, 20, CANVAS_W - x - RIGHT);
        List<Line> lines = new ArrayList<>();
        double y = start;
        boolean lead = true;
        for (String text : wrapped) {
            lines.add(new Line(lead ? "" : num(x), num(y), text, fontFor(text), lead));
            lead = false;
            y += 28;
        }
        return List.copyOf(lines);
    }

    private static IconRow iconRow(List<String> modes, double startX, double centerY, double scale, double step, double fontSize)
    {
        if (modes == null || modes.isEmpty()) {
            return new IconRow(List.of(), startX, startX + fontSize * 0.72);
        }
        List<PlacedMode> placed = new ArrayList<>();
        for (int i = 0; i < modes.size(); i++) {
            String mode = modes.get(i);
            double centerX = startX + i * step;
            placed.add(new PlacedMode(ModWikiModes.symbolId(mode), modeTransform(mode, centerX, centerY, scale)));
        }
        double lastCenter = startX + (modes.size() - 1L) * step;
        double textX = lastCenter + 21.62 * scale + 4;
        return new IconRow(List.copyOf(placed), textX, textX + fontSize * 0.72);
    }

    private static String modeTransform(String mode, double centerX, double centerY, double scale)
    {
        return String.format(Locale.ROOT,
                "translate(%.3f %.3f) scale(%.3f) translate(%.3f %.3f)",
                centerX,
                centerY,
                scale,
                -ModWikiModes.originX(mode),
                -50.900);
    }

    private static UnrankedNote unrankedNote(List<String> modes, double badgeRight)
    {
        if (modes.isEmpty()) {
            return UnrankedNote.hidden();
        }
        double openX = badgeRight + 14;
        double iconsStart = openX + textWidth("(非", 22);
        IconRow icons = iconRow(modes, iconsStart + 9, 52, 0.36, 24, 22);
        double closeX = iconsStart + modes.size() * 24 + 2;
        return new UnrankedNote(true, num(openX), num(closeX), icons.modes());
    }

    private static double unrankedWidth(List<String> modes)
    {
        if (modes.isEmpty()) {
            return 0;
        }
        return 14 + textWidth("(非)", 22) + modes.size() * 24 + 8;
    }

    private static Chip hotkeyChip(String hotkey, double baseline)
    {
        double x = hotkeyValueX();
        double width = textWidth(hotkey, 20) + 14;
        return new Chip(num(x - 6), num(baseline - 18), num(width), "26");
    }

    private static double hotkeyValueX()
    {
        return valueX(HOTKEY) + 12;
    }

    private static String iconTransform(ModWikiIcon icon)
    {
        double scale = Math.min(SLOT_W / icon.width(), SLOT_H / icon.height());
        double tx = SLOT_X + (SLOT_W - icon.width() * scale) / 2 - icon.minX() * scale;
        double ty = SLOT_Y + (SLOT_H - icon.height() * scale) / 2 - icon.minY() * scale;
        return String.format(Locale.ROOT, "translate(%.3f %.3f) scale(%.4f)", tx, ty, scale);
    }

    private static TitleFit fitTitle(String text, ModWikiDocument.Rank rank, double modesLeft, double noteWidth)
    {
        double artwork = artworkWidth(rank);
        double size = 42;
        double width = monoWidth(text, size);
        double badgeX = 24 + width + 14;
        double limit = modesLeft - 16;
        while (size > 28 && badgeX + artwork + noteWidth > limit) {
            size -= 2;
            width = monoWidth(text, size);
            badgeX = 24 + width + 14;
        }
        return new TitleFit(text, size, badge(rank, badgeX, artwork));
    }

    private static double artworkWidth(ModWikiDocument.Rank rank)
    {
        return switch (rank) {
            case RANKED -> 101;
            case PARTIAL -> 191;
            case UNRANKED -> 128;
        };
    }

    private static Badge badge(ModWikiDocument.Rank rank, double left, double width)
    {
        String transform = String.format(Locale.ROOT, "translate(%.3f 0)", left - 230);
        return switch (rank) {
            case RANKED -> new Badge(transform, "1", "0", "0", num(left), num(width));
            case PARTIAL -> new Badge(transform, "0", "1", "0", num(left), num(width));
            case UNRANKED -> new Badge(transform, "0", "0", "1", num(left), num(width));
        };
    }

    private static String pageTitle(ModWikiDocument page)
    {
        if (page.name().isBlank()) {
            return page.acronym();
        }
        String suffix = "(" + page.acronym() + ")";
        if (page.name().contains(suffix)) {
            return page.name();
        }
        return page.name() + " " + suffix;
    }

    private static double modesLeft(List<String> modes)
    {
        if (modes.isEmpty()) {
            return CANVAS_W;
        }
        double leftmost = 950 - (modes.size() - 1L) * 56;
        return leftmost - 22;
    }

    private static double valueX(String label)
    {
        return LABEL_X + textWidth(label, 20) + 2;
    }

    private static List<String> wrap(String text, double size, double maxWidth)
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
                double charWidth = codePoint > 0xFF ? size : codePoint == ' ' ? size * 0.28 : size * 0.56;
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

    private static double textWidth(String text, double size)
    {
        return textWidth(text, size, 0.56);
    }

    private static double textWidth(String text, double size, double latinFactor)
    {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        double width = 0;
        for (int index = 0; index < text.length(); ) {
            int codePoint = text.codePointAt(index);
            index += Character.charCount(codePoint);
            width += codePoint > 0xFF ? size : codePoint == ' ' ? size * 0.28 : size * latinFactor;
        }
        return width;
    }

    private static double monoWidth(String text, double size)
    {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        double width = 0;
        for (int index = 0; index < text.length(); ) {
            int codePoint = text.codePointAt(index);
            index += Character.charCount(codePoint);
            width += isCjk(codePoint) ? size : size * 0.612;
        }
        return width;
    }

    private static String fontFor(String content)
    {
        if (content == null || content.isBlank()) {
            return LATIN;
        }
        return content.codePoints().anyMatch(ModWikiPoster::isCjk) ? CJK : LATIN;
    }

    private static boolean isCjk(int codePoint)
    {
        Character.UnicodeScript script = Character.UnicodeScript.of(codePoint);
        return script == Character.UnicodeScript.HAN
                || script == Character.UnicodeScript.HIRAGANA
                || script == Character.UnicodeScript.KATAKANA
                || script == Character.UnicodeScript.HANGUL;
    }

    private static double baseline(String value)
    {
        return Double.parseDouble(value);
    }

    private static String num(double value)
    {
        return String.format(Locale.ROOT, "%.3f", value);
    }

    private record TitleFit(String text, double size, Badge badge)
    {
    }

    private record IconRow(List<PlacedMode> modes, double textX, double bodyX)
    {
    }
}
