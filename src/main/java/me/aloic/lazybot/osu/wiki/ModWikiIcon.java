package me.aloic.lazybot.osu.wiki;

/**
 * Icon carried inside a mod wiki page.
 * {@code markup} is the inner SVG, {@code viewBox} is that artwork's own box,
 * and {@code transform} is an extra translate or scale in that box, applied before the icon is fitted into the hexagon.
 */
public record ModWikiIcon(
        String markup,
        double minX,
        double minY,
        double width,
        double height,
        String transform)
{
    public ModWikiIcon
    {
        markup = markup == null ? "" : markup.trim();
        transform = transform == null || transform.isBlank() ? "translate(0 0)" : transform.trim();
    }

    public boolean present()
    {
        return !markup.isBlank() && width > 0 && height > 0;
    }
}
