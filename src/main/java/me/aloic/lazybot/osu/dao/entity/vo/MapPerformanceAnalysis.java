package me.aloic.lazybot.osu.dao.entity.vo;

import java.util.List;

/** Complete calculation result for the map PP analysis panel. */
public record MapPerformanceAnalysis(
        BeatmapStatistics context,
        double targetAccuracy,
        double starRating,
        List<AlgorithmSnapshot> history,
        List<CurvePoint> missCurve,
        List<CurvePoint> accuracyCurve
) {
    public MapPerformanceAnalysis
    {
        history = List.copyOf(history);
        missCurve = List.copyOf(missCurve);
        accuracyCurve = List.copyOf(accuracyCurve);
    }

    public record AlgorithmSnapshot(
            String algorithm,
            double pp,
            double absoluteChange,
            double relativeChange,
            List<PpComponent> components
    ) {
        public AlgorithmSnapshot {
            components = List.copyOf(components);
        }
    }

    public record PpComponent(
            String name,
            String color,
            double pp,
            double ratio
    ) {}

    public record CurvePoint(
            double input,
            double pp,
            double absoluteLoss,
            double relativeLoss
    ) {}
}
