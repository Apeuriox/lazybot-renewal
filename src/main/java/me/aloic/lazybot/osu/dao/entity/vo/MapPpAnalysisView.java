package me.aloic.lazybot.osu.dao.entity.vo;

import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.utils.ColorUtil;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Precomputed coordinates and labels consumed by the JTE SVG template. */
public record MapPpAnalysisView(
        int width,
        int height,
        MapPerformanceAnalysis analysis,
        String title,
        String scenario,
        String generatedAt,
        String status,
        String starBackgroundColor,
        String starTextColor,
        List<HistoryBar> historyBars,
        List<BreakdownRow> breakdownRows,
        LineChart missChart,
        LineChart accuracyChart,
        String cumulativeChange,
        String largestChange
) {
    public static final int WIDTH = 1400;
    public static final int HEIGHT = 1520;

    public static MapPpAnalysisView from(MapPerformanceAnalysis analysis) {
        BeatmapPerformance beatmap = analysis.context().getBeatmap();
        String title = beatmap.getArtist() + " — " + beatmap.getTitle()
                + " [" + beatmap.getVersion() + "]";
        String mods = analysis.context().getImaginaryMods().stream()
                .map(Mod::getAcronym)
                .filter(acronym -> !"DA".equals(acronym))
                .reduce("", String::concat);
        if (mods.isBlank()) {
            mods = "NM";
        }
        String scenario = mods;
        String generatedAt = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd / HH:mm:ss"));
        String status = beatmap.getStatus() == null
                ? ""
                : beatmap.getStatus().toLowerCase(Locale.ROOT);
        String starBackgroundColor = "#"
                + ColorUtil.getDifficultyBackgroundColor(analysis.starRating());
        String starTextColor = ColorUtil.getDifficultyColor(analysis.starRating());

        List<HistoryBar> historyBars = historyBars(analysis.history());
        List<BreakdownRow> breakdownRows = breakdownRows(analysis.history());
        LineChart missChart = lineChart(analysis.missCurve(), 73, 1186, 600, 200, false);
        LineChart accuracyChart = lineChart(analysis.accuracyCurve(), 785, 1186, 600, 200, true);

        MapPerformanceAnalysis.AlgorithmSnapshot first = analysis.history().getFirst();
        MapPerformanceAnalysis.AlgorithmSnapshot latest = analysis.history().getLast();
        double cumulative = latest.pp() - first.pp();
        double cumulativePercent = first.pp() == 0.0 ? 0.0 : cumulative / first.pp() * 100.0;
        String cumulativeChange = String.format(
                Locale.ROOT, "%+.1f / %+.2f%%", cumulative, cumulativePercent);
        MapPerformanceAnalysis.AlgorithmSnapshot largest = analysis.history().stream()
                .skip(1)
                .max((left, right) -> Double.compare(
                        Math.abs(left.absoluteChange()), Math.abs(right.absoluteChange())))
                .orElse(latest);
        String largestChange = String.format(
                Locale.ROOT, "%s: %+.1f", largest.algorithm(), largest.absoluteChange());

        return new MapPpAnalysisView(
                WIDTH, HEIGHT, analysis, title, scenario, generatedAt,
                status, starBackgroundColor, starTextColor,
                historyBars, breakdownRows, missChart, accuracyChart,
                cumulativeChange, largestChange);
    }

    private static List<HistoryBar> historyBars(
            List<MapPerformanceAnalysis.AlgorithmSnapshot> history) {
        double minimum = history.stream()
                .mapToDouble(MapPerformanceAnalysis.AlgorithmSnapshot::pp)
                .min()
                .orElse(0.0);
        double maximum = history.stream()
                .mapToDouble(MapPerformanceAnalysis.AlgorithmSnapshot::pp)
                .max()
                .orElse(1.0);
        double plotBottom = 943;
        double minimumBarHeight = 70;
        double variableHeight = 173;
        double range = maximum - minimum;
        double barWidth = 50;
        double gap = 70;
        double startX = 59;
        List<HistoryBar> result = new ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            MapPerformanceAnalysis.AlgorithmSnapshot item = history.get(i);
            double height = range < 0.001
                    ? minimumBarHeight + variableHeight / 2.0
                    : minimumBarHeight + (item.pp() - minimum) / range * variableHeight;
            result.add(new HistoryBar(
                    startX + i * (barWidth + gap),
                    plotBottom - height,
                    barWidth,
                    height,
                    item.algorithm(),
                    formatPp(item.pp()),
                    i == 0 ? "BASELINE" : signed(item.absoluteChange()) + "pp / "
                            + signed(item.relativeChange()) + "%",
                    i == history.size() - 1));
        }
        return List.copyOf(result);
    }

    private static List<BreakdownRow> breakdownRows(
            List<MapPerformanceAnalysis.AlgorithmSnapshot> history) {
        List<BreakdownRow> rows = new ArrayList<>();
        double y = 698;
        for (MapPerformanceAnalysis.AlgorithmSnapshot snapshot : history) {
            double x = 837;
            List<BreakdownSegment> segments = new ArrayList<>();
            for (MapPerformanceAnalysis.PpComponent component : snapshot.components()) {
                double width = component.ratio() / 100.0 * 540.0;
                segments.add(new BreakdownSegment(
                        x, y, width, 26,
                        component.color(), component.name(), component.pp(), component.ratio()));
                x += width;
            }
            rows.add(new BreakdownRow(snapshot.algorithm(), formatPp(snapshot.pp()), segments));
            y += 55;
        }
        return List.copyOf(rows);
    }

    private static LineChart lineChart(
            List<MapPerformanceAnalysis.CurvePoint> values,
            double x,
            double y,
            double width,
            double height,
            boolean accuracy) {
        double rawMin = values.stream().mapToDouble(MapPerformanceAnalysis.CurvePoint::pp).min().orElse(0.0);
        double rawMax = values.stream().mapToDouble(MapPerformanceAnalysis.CurvePoint::pp).max().orElse(1.0);
        double range = Math.max(1.0, rawMax - rawMin);
        double minimum = Math.max(0.0, rawMin - range * 0.12);
        double maximum = rawMax + range * 0.12;

        List<LinePoint> points = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            MapPerformanceAnalysis.CurvePoint value = values.get(i);
            double pointX = x + i * width / (values.size() - 1.0);
            double pointY = y + height - (value.pp() - minimum) / (maximum - minimum) * height;
            boolean labelled = accuracy
                    ? isAccuracyLabel(value.input())
                    : value.input() <= 3 || value.input() == 5
                            || value.input() == 10 || value.input() == 15 || value.input() == 20;
            points.add(new LinePoint(
                    pointX, pointY, value.input(), value.pp(),
                    value.absoluteLoss(), value.relativeLoss(), labelled));
        }

        List<LineSegment> segments = new ArrayList<>();
        for (int i = 1; i < points.size(); i++) {
            LinePoint previous = points.get(i - 1);
            LinePoint current = points.get(i);
            segments.add(new LineSegment(previous.x(), previous.y(), current.x(), current.y()));
        }

        List<AxisTick> yTicks = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            double tickY = y + height - i * height / 4.0;
            double value = minimum + i * (maximum - minimum) / 4.0;
            yTicks.add(new AxisTick(tickY, formatPp(value)));
        }
        return new LineChart(x, y, width, height, points, segments, yTicks);
    }

    private static boolean isAccuracyLabel(double value) {
        return value == 100.0 || value == 99.0 || value == 98.0
                || value == 97.0 || value == 95.0 || value == 90.0;
    }

    public static String formatPp(double value) {
        return String.format(Locale.ROOT, "%.2fpp", value);
    }

    public static String signed(double value) {
        return String.format(Locale.ROOT, "%+.2f", value);
    }

    public static String formatDuration(Integer seconds) {
        if (seconds == null || seconds < 0) {
            return "--:--";
        }
        return String.format(Locale.ROOT, "%02d:%02d", seconds / 60, seconds % 60);
    }

    public record HistoryBar(
            double x, double y, double width, double height,
            String algorithm, String pp, String change, boolean latest) {}

    public record BreakdownRow(
            String algorithm, String totalPp, List<BreakdownSegment> segments) {
        public BreakdownRow {
            segments = List.copyOf(segments);
        }
    }

    public record BreakdownSegment(
            double x, double y, double width, double height,
            String color, String name, double pp, double ratio) {}

    public record LineChart(
            double x, double y, double width, double height,
            List<LinePoint> points,
            List<LineSegment> segments,
            List<AxisTick> yTicks) {
        public LineChart {
            points = List.copyOf(points);
            segments = List.copyOf(segments);
            yTicks = List.copyOf(yTicks);
        }
    }

    public record LinePoint(
            double x, double y, double input, double pp,
            double loss, double lossPercent, boolean labelled) {}

    public record LineSegment(double x1, double y1, double x2, double y2) {}

    public record AxisTick(double y, String label) {}
}
