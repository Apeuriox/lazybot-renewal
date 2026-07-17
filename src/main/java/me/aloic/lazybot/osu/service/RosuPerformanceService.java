package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapStatistics;
import me.aloic.lazybot.osu.dao.entity.vo.MapScore;
import me.aloic.lazybot.osu.dao.entity.vo.PerformanceVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.rosupp.AlgorithmVersion;
import me.aloic.rosupp.DifficultyRequest;
import me.aloic.rosupp.DifficultyResult;
import me.aloic.rosupp.PerformanceRequest;
import me.aloic.rosupp.PerformanceResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Application boundary for rosu-pp calculations.
 *
 * <p>The service deliberately exposes only immutable request/result objects. Native-backed
 * calculators and beatmaps are created and closed inside the implementation. Backend exceptions,
 * including {@code RosuPpException} and {@code UnsupportedOptionException}, are intentionally
 * propagated unchanged so callers can distinguish invalid input from unsupported options.</p>
 */
public interface RosuPerformanceService
{
    AlgorithmVersion defaultAlgorithm();

    DifficultyResult calculateDifficulty(Path beatmapPath, DifficultyRequest request);

    DifficultyResult calculateDifficulty(Path beatmapPath, AlgorithmVersion algorithm, DifficultyRequest request);

    DifficultyResult calculateDifficulty(byte[] beatmapBytes, DifficultyRequest request);

    DifficultyResult calculateDifficulty(byte[] beatmapBytes, AlgorithmVersion algorithm, DifficultyRequest request);

    PerformanceResult calculatePerformance(Path beatmapPath, PerformanceRequest request);

    PerformanceResult calculatePerformance(Path beatmapPath, AlgorithmVersion algorithm, PerformanceRequest request);

    PerformanceResult calculatePerformance(byte[] beatmapBytes, PerformanceRequest request);

    PerformanceResult calculatePerformance(byte[] beatmapBytes, AlgorithmVersion algorithm, PerformanceRequest request);

    PerformanceVO calculatePerformance(Path beatmapPath, ScoreVO score);

    PerformanceVO calculatePerformance(Path beatmapPath, ScoreLazerDTO score);

    PerformanceVO calculateCurrentPerformance(Path beatmapPath, ScoreLazerDTO score);

    PerformanceVO calculatePerformance(Path beatmapPath, ScoreSequence score);

    double recalculatePerformance(Path beatmapPath, MapScore score);

    DifficultyResult calculateDifficulty(Path beatmapPath, String mode);

    void setupMapScorePerformances(Path beatmapPath, List<MapScore> scores);

    void setupBeatmapStatistics(BeatmapStatistics beatmapStatistics) throws IOException;
}
