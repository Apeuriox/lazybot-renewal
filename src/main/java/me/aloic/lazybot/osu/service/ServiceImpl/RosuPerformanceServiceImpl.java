package me.aloic.lazybot.osu.service.ServiceImpl;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapStatistics;
import me.aloic.lazybot.osu.dao.entity.vo.ImaginaryPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.MapScore;
import me.aloic.lazybot.osu.dao.entity.vo.PerformanceVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.service.RosuPerformanceService;
import me.aloic.lazybot.osu.utils.AssetDownloadUtil;
import me.aloic.lazybot.osu.enums.OsuMod;
import me.aloic.lazybot.util.CommonTool;
import me.aloic.rosupp.AlgorithmVersion;
import me.aloic.rosupp.Beatmap;
import me.aloic.rosupp.DifficultyRequest;
import me.aloic.rosupp.DifficultyResult;
import me.aloic.rosupp.GameMode;
import me.aloic.rosupp.Mods;
import me.aloic.rosupp.PerformanceRequest;
import me.aloic.rosupp.PerformanceResult;
import me.aloic.rosupp.RosuPp;
import me.aloic.rosupp.ScoreMode;
import me.aloic.rosupp.UnsupportedOptionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

@Service
@Slf4j
public class RosuPerformanceServiceImpl implements RosuPerformanceService
{
    private static final String LATEST_ALGORITHM_KEY = "rework-20260706-9a073d2";
    private static final List<Integer> SCORE_ACCURACIES = List.of(100, 99, 98, 97, 95);
    private static final List<Integer> MAP_ACCURACIES = List.of(100, 99, 98, 97, 95, 93);

    private final AlgorithmVersion defaultAlgorithm;

    public RosuPerformanceServiceImpl(
            @Value("${lazybot.rosu.algorithm-version:" + LATEST_ALGORITHM_KEY + "}") String algorithmKey)
    {
        this.defaultAlgorithm = resolveAlgorithm(algorithmKey);
    }

    @Override
    public AlgorithmVersion defaultAlgorithm()
    {
        return defaultAlgorithm;
    }

    @Override
    public DifficultyResult calculateDifficulty(Path beatmapPath, DifficultyRequest request)
    {
        return calculateDifficulty(beatmapPath, defaultAlgorithm, request);
    }

    @Override
    public DifficultyResult calculateDifficulty(
            Path beatmapPath, AlgorithmVersion algorithm, DifficultyRequest request)
    {
        Objects.requireNonNull(beatmapPath, "beatmapPath");
        Objects.requireNonNull(request, "request");
        return withBeatmap(algorithm, beatmapPath,
                (calculator, beatmap) -> calculator.calculateDifficulty(beatmap, request));
    }

    @Override
    public DifficultyResult calculateDifficulty(byte[] beatmapBytes, DifficultyRequest request)
    {
        return calculateDifficulty(beatmapBytes, defaultAlgorithm, request);
    }

    @Override
    public DifficultyResult calculateDifficulty(
            byte[] beatmapBytes, AlgorithmVersion algorithm, DifficultyRequest request)
    {
        Objects.requireNonNull(beatmapBytes, "beatmapBytes");
        Objects.requireNonNull(request, "request");
        return withBeatmap(algorithm, beatmapBytes,
                (calculator, beatmap) -> calculator.calculateDifficulty(beatmap, request));
    }

    @Override
    public PerformanceResult calculatePerformance(Path beatmapPath, PerformanceRequest request)
    {
        return calculatePerformance(beatmapPath, defaultAlgorithm, request);
    }

    @Override
    public PerformanceResult calculatePerformance(
            Path beatmapPath, AlgorithmVersion algorithm, PerformanceRequest request)
    {
        Objects.requireNonNull(beatmapPath, "beatmapPath");
        Objects.requireNonNull(request, "request");
        return withBeatmap(algorithm, beatmapPath,
                (calculator, beatmap) -> calculator.calculatePerformance(beatmap, request));
    }

    @Override
    public PerformanceResult calculatePerformance(byte[] beatmapBytes, PerformanceRequest request)
    {
        return calculatePerformance(beatmapBytes, defaultAlgorithm, request);
    }

    @Override
    public PerformanceResult calculatePerformance(
            byte[] beatmapBytes, AlgorithmVersion algorithm, PerformanceRequest request)
    {
        Objects.requireNonNull(beatmapBytes, "beatmapBytes");
        Objects.requireNonNull(request, "request");
        return withBeatmap(algorithm, beatmapBytes,
                (calculator, beatmap) -> calculator.calculatePerformance(beatmap, request));
    }

    @Override
    public PerformanceVO calculatePerformance(Path beatmapPath, ScoreVO score)
    {
        return calculatePerformance(beatmapPath, score, defaultAlgorithm);
    }

    @Override
    public PerformanceVO calculatePerformance(Path beatmapPath, ScoreVO score, AlgorithmVersion algorithm)
    {
        AlgorithmVersion selected = algorithmOrDefault(algorithm);
        return calculateScore(beatmapPath, selected, score.getModJSON(), score.getStatistics(),
                score.getMode(), score.getMaxCombo(), score.getIsLazer(), score.getScore(), true);
    }

    @Override
    public PerformanceVO calculatePerformance(Path beatmapPath, ScoreLazerDTO score)
    {
        return calculatePerformance(beatmapPath, score, defaultAlgorithm);
    }

    @Override
    public PerformanceVO calculatePerformance(Path beatmapPath, ScoreLazerDTO score, AlgorithmVersion algorithm)
    {
        AlgorithmVersion selected = algorithmOrDefault(algorithm);
        return calculateScore(beatmapPath, selected, score.getMods(), score.getStatistics(),
                String.valueOf(score.getRuleset_id()), score.getMax_combo(), score.getLegacy_total_score() == 0,
                score.getLegacy_total_score(), true);
    }

    @Override
    public PerformanceVO calculateCurrentPerformance(Path beatmapPath, ScoreLazerDTO score)
    {
        return calculateCurrentPerformance(beatmapPath, score, defaultAlgorithm);
    }

    @Override
    public PerformanceVO calculateCurrentPerformance(Path beatmapPath, ScoreLazerDTO score, AlgorithmVersion algorithm)
    {
        AlgorithmVersion selected = algorithmOrDefault(algorithm);
        return calculateScore(beatmapPath, selected, score.getMods(), score.getStatistics(),
                String.valueOf(score.getRuleset_id()), score.getMax_combo(), score.getLegacy_total_score() == 0,
                score.getLegacy_total_score(), false);
    }

    @Override
    public PerformanceVO calculatePerformance(Path beatmapPath, ScoreSequence score)
    {
        return calculatePerformance(beatmapPath, score, defaultAlgorithm);
    }

    @Override
    public PerformanceVO calculatePerformance(Path beatmapPath, ScoreSequence score, AlgorithmVersion algorithm)
    {
        AlgorithmVersion selected = algorithmOrDefault(algorithm);
        return calculateScore(beatmapPath, selected, score.getModList(), score.getStatistics(),
                String.valueOf(score.getRulesetId()), score.getMaxCombo(), score.getIsLazer(), score.getScore(), true);
    }

    @Override
    public double recalculatePerformance(Path beatmapPath, MapScore score)
    {
        return withBeatmap(defaultAlgorithm, beatmapPath, (calculator, beatmap) ->
                calculateScorePerformance(calculator, beatmap, defaultAlgorithm, score.getModJSON(),
                        score.getStatistics(), "osu", score.getMaxCombo(), score.getIsLazer(), score.getScore()).pp());
    }

    @Override
    public DifficultyResult calculateDifficulty(Path beatmapPath, String mode)
    {
        return calculateDifficulty(beatmapPath, difficultyRequest(defaultAlgorithm, null, mode, true));
    }

    @Override
    public void setupMapScorePerformances(Path beatmapPath, List<MapScore> scores)
    {
        withBeatmap(defaultAlgorithm, beatmapPath, (calculator, beatmap) -> {
            for (MapScore score : scores) {
                PerformanceResult performance = calculateScorePerformance(calculator, beatmap, defaultAlgorithm,
                        score.getModJSON(), score.getStatistics(), "osu", score.getMaxCombo(),
                        score.getIsLazer(), score.getScore());
                if (score.getPp() == null) {
                    score.setPp(performance.pp());
                }
                score.setStarRating(performance.difficulty().stars());
                score.setIffc(calculateScorePerformance(calculator, beatmap, defaultAlgorithm,
                        score.getModJSON(), score.getStatistics(), "osu", 0, score.getIsLazer(), null).pp());
            }
            return null;
        });
    }

    @Override
    public void setupBeatmapStatistics(BeatmapStatistics beatmapStatistics) throws IOException
    {
        setupBeatmapStatistics(beatmapStatistics, defaultAlgorithm);
    }

    @Override
    public void setupBeatmapStatistics(
            BeatmapStatistics beatmapStatistics, AlgorithmVersion algorithm) throws IOException
    {
        AlgorithmVersion selectedAlgorithm = algorithmOrDefault(algorithm);
        Path beatmapPath = AssetDownloadUtil.beatmapPath(beatmapStatistics.getBeatmap().getBid(), false);
        String mode = beatmapStatistics.getBeatmap().getMode().getDescribe();

        withBeatmap(selectedAlgorithm, beatmapPath, (calculator, beatmap) -> {
            DifficultyRequest difficultyRequest = difficultyRequest(
                    selectedAlgorithm, beatmapStatistics.getImaginaryMods(), mode, true);
            DifficultyResult difficulty = calculator.calculateDifficulty(beatmap, difficultyRequest);
            PerformanceResult maximumPerformance = calculator.calculatePerformance(beatmap,
                    PerformanceRequest.builder(difficultyRequest).accuracy(100.0).build());

            beatmapStatistics.getBeatmap().setDifficultyAttributes(difficulty);
            beatmapStatistics.getBeatmap().setLengthBonus(CommonTool.lengthBonusCalc(
                    beatmapStatistics.getBeatmap().getCountCircles()
                            + beatmapStatistics.getBeatmap().getCountSliders()
                            + beatmapStatistics.getBeatmap().getCountSpinners()));

            ImaginaryPerformance imaginary = new ImaginaryPerformance();
            imaginary.setAccPPList(calculateAccuracyPpList(
                    calculator, beatmap, difficultyRequest, MAP_ACCURACIES));
            imaginary.setAimPP(maximumPerformance.ppAim());
            imaginary.setAccPP(maximumPerformance.ppAccuracy());
            imaginary.setSpdPP(maximumPerformance.ppSpeed());
            imaginary.setReadPP(maximumPerformance.readingPerformanceOptional().orElse(0.0));
            imaginary.setFlashlightPP(maximumPerformance.ppFlashlight());
            imaginary.setStar(difficulty.stars());
            imaginary.setAimStar(difficulty.aim());
            imaginary.setSpeedStar(difficulty.speed());
            imaginary.setReadStar(difficulty.readingOptional().orElse(0.0));
            imaginary.setImaginaryAccuracy(beatmapStatistics.getPerformance().getImaginaryAccuracy());
            imaginary.setImaginaryPP(calculateAccuracyPerformance(calculator, beatmap, difficultyRequest,
                    beatmapStatistics.getPerformance().getImaginaryAccuracy()).pp());
            beatmapStatistics.setPerformance(imaginary);
            return null;
        });
    }

    private PerformanceVO calculateScore(
            Path beatmapPath,
            AlgorithmVersion algorithm,
            List<Mod> mods,
            ScoreStatisticsLazer statistics,
            String mode,
            Integer maxCombo,
            boolean lazer,
            Long legacyTotalScore,
            boolean includeProjections)
    {
        return withBeatmap(algorithm, beatmapPath, (calculator, beatmap) -> {
            PerformanceResult performance = calculateScorePerformance(
                    calculator, beatmap, algorithm, mods, statistics, mode, maxCombo, lazer, legacyTotalScore);
            PerformanceVO result = new PerformanceVO();
            result.setStar(performance.difficulty().stars());
            result.setCurrentPP(performance.pp());
            result.setOriginal(performance);

            if (!includeProjections) {
                return result;
            }

            DifficultyRequest difficultyRequest = difficultyRequest(algorithm, mods, mode, lazer);
            result.setAccPPList(calculateAccuracyPpList(
                    calculator, beatmap, difficultyRequest, SCORE_ACCURACIES));
            result.setIfFc(calculateScorePerformance(
                    calculator, beatmap, algorithm, mods, statistics, mode, 0, lazer, null).pp());

            PerformanceResult maximumPerformance = calculateAccuracyPerformance(
                    calculator, beatmap, difficultyRequest, 100.0);
            result.setAimPPMax(maximumPerformance.ppAim());
            result.setSpdPPMax(maximumPerformance.ppSpeed());
            result.setAccPPMax(maximumPerformance.ppAccuracy());
            result.setReadPPMax(maximumPerformance.readingPerformanceOptional().orElse(0.0));
            result.setFlashlightPPMax(maximumPerformance.ppFlashlight());
            result.setAimPP(performance.ppAim());
            result.setSpdPP(performance.ppSpeed());
            result.setAccPP(performance.ppAccuracy());
            result.setReadPP(performance.readingPerformanceOptional().orElse(0.0));
            result.setFlashlightPP(performance.ppFlashlight());
            return result;
        });
    }

    private PerformanceResult calculateScorePerformance(
            RosuPp calculator,
            Beatmap beatmap,
            AlgorithmVersion algorithm,
            List<Mod> mods,
            ScoreStatisticsLazer statistics,
            String mode,
            Integer maxCombo,
            boolean lazer,
            Long legacyTotalScore)
    {
        GameMode gameMode = toGameMode(mode);
        DifficultyRequest difficultyRequest = difficultyRequest(algorithm, mods, gameMode, lazer);
        PerformanceRequest.Builder builder = PerformanceRequest.builder(difficultyRequest);
        int combo = Optional.ofNullable(maxCombo).orElse(0);

        if (combo != 0) {
            builder.combo(combo);
        }

        switch (gameMode) {
            case OSU -> {
                builder.n300(orZero(statistics.getGreat()))
                        .n100(orZero(statistics.getOk()))
                        .n50(orZero(statistics.getMeh()));
                if (combo != 0) {
                    builder.misses(orZero(statistics.getMiss()));
                }
                if (lazer && algorithm != AlgorithmVersion.PRECSR_202210) {
                    builder.largeTickHits(orZero(statistics.getLarge_tick_hit()))
                            .sliderEndHits(orZero(statistics.getSlider_tail_hit()));
                }
            }
            case TAIKO -> {
                builder.n300(orZero(statistics.getGreat()))
                        .n100(orZero(statistics.getOk()));
                if (combo != 0) {
                    builder.misses(orZero(statistics.getMiss()));
                }
            }
            case MANIA -> builder.nGeki(orZero(statistics.getPerfect()))
                    .n300(orZero(statistics.getGreat()))
                    .nKatu(orZero(statistics.getGood()))
                    .n100(orZero(statistics.getOk()))
                    .n50(orZero(statistics.getMeh()))
                    .misses(orZero(statistics.getMiss()));
            case CATCH -> {
                builder.n300(orZero(statistics.getGreat()))
                        .n100(orZero(statistics.getLarge_tick_hit()))
                        .n50(orZero(statistics.getSmall_tick_hit()))
                        .nKatu(orZero(statistics.getSmall_tick_miss()));
                if (combo != 0) {
                    builder.misses(orZero(statistics.getMiss()));
                }
            }
        }
        if (combo != 0
                && legacyTotalScore != null
                && supportsLegacyTotalScore(algorithm, gameMode)) {
            if (legacyTotalScore < 0 || legacyTotalScore > Integer.MAX_VALUE) {
                throw new UnsupportedOptionException("legacyTotalScore 超出 FFM 支持的整数范围: " + legacyTotalScore);
            }
            builder.legacyTotalScore(legacyTotalScore.intValue());
        }
        return calculator.calculatePerformance(beatmap, builder.build());
    }

    private static PerformanceResult calculateAccuracyPerformance(
            RosuPp calculator, Beatmap beatmap, DifficultyRequest difficultyRequest, double accuracy)
    {
        return calculator.calculatePerformance(beatmap,
                PerformanceRequest.builder(difficultyRequest).accuracy(accuracy).build());
    }

    private static Map<Integer, Double> calculateAccuracyPpList(
            RosuPp calculator,
            Beatmap beatmap,
            DifficultyRequest difficultyRequest,
            List<Integer> accuracies)
    {
        Map<Integer, Double> result = new LinkedHashMap<>();
        for (Integer accuracy : accuracies) {
            result.put(accuracy,
                    calculateAccuracyPerformance(calculator, beatmap, difficultyRequest, accuracy).pp());
        }
        return result;
    }

    private static DifficultyRequest difficultyRequest(
            AlgorithmVersion algorithm, List<Mod> mods, String mode, boolean lazer)
    {
        return difficultyRequest(algorithm, mods, toGameMode(mode), lazer);
    }

    private static DifficultyRequest difficultyRequest(
            AlgorithmVersion algorithm, List<Mod> mods, GameMode mode, boolean lazer)
    {
        DifficultyRequest.Builder builder = DifficultyRequest.builder().mode(mode);
        if (algorithm == AlgorithmVersion.PRECSR_202210) {
            builder.mods(toLegacyMods(mods, lazer));
        }
        else {
            if(mode == GameMode.OSU || mode == GameMode.MANIA) {
                builder.scoreMode(lazer ? ScoreMode.LAZER : ScoreMode.STABLE);
            }

            if (mods != null && !mods.isEmpty()) {
                builder.modsJson(JSONUtil.toJsonStr(mods));
            }
        }
        return builder.build();
    }

    private static Mods toLegacyMods(List<Mod> mods, boolean lazer)
    {
        long bits = 0;
        if (mods == null) {
            return Mods.NONE;
        }
        for (Mod mod : mods) {
            if (mod == null || mod.getAcronym() == null || mod.getAcronym().isBlank()) {
                log.warn("[PP重算] PRECSR_202210 忽略缺少 acronym 的 Mod: {}", mod);
                continue;
            }
            // osu! API v2 marks stable scores with CL. PRECSR already uses classic scoring
            // semantics and has no CL bit, so this marker is represented by the omitted ScoreMode.
            if (!lazer && "CL".equalsIgnoreCase(mod.getAcronym())) {
                continue;
            }
            OsuMod osuMod = OsuMod.getModEnum(mod.getAcronym());
            if (mod.getSettings() != null || osuMod == OsuMod.Other || osuMod.getValue() < 0) {
                log.warn("[PP重算] PRECSR_202210 忽略不支持的 Mod: acronym={}, settings={}",
                        mod.getAcronym(), mod.getSettings());
                continue;
            }
            bits |= Integer.toUnsignedLong(osuMod.getValue());
        }
        return new Mods(bits);
    }

    private static boolean supportsLegacyTotalScore(AlgorithmVersion algorithm, GameMode mode)
    {
        return mode == GameMode.OSU
                && (algorithm == AlgorithmVersion.REWORK_202510
                    || algorithm == AlgorithmVersion.REWORK_20260706);
    }

    private static GameMode toGameMode(String mode)
    {
        return switch (me.aloic.lazybot.osu.enums.OsuMode.getMode(mode)) {
            case Osu -> GameMode.OSU;
            case Taiko -> GameMode.TAIKO;
            case Catch -> GameMode.CATCH;
            case Mania -> GameMode.MANIA;
            case Default -> throw new LazybotRuntimeException("Unsupported mode: " + mode);
        };
    }

    private static int orZero(Integer value)
    {
        return Optional.ofNullable(value).orElse(0);
    }

    private AlgorithmVersion algorithmOrDefault(AlgorithmVersion algorithm)
    {
        return algorithm != null ? algorithm : defaultAlgorithm;
    }

    private static AlgorithmVersion resolveAlgorithm(String stableKey)
    {
        for (AlgorithmVersion algorithm : AlgorithmVersion.values()) {
            if (algorithm.stableKey().equals(stableKey) || algorithm.name().equalsIgnoreCase(stableKey)) {
                return algorithm;
            }
        }
        throw new IllegalArgumentException("Unsupported rosu-pp algorithm version: " + stableKey);
    }

    private static <T> T withBeatmap(
            AlgorithmVersion algorithm,
            Path beatmapPath,
            BiFunction<RosuPp, Beatmap, T> operation)
    {
        Objects.requireNonNull(algorithm, "algorithm");
        try (RosuPp calculator = RosuPp.forVersion(algorithm);
             Beatmap beatmap = calculator.loadBeatmap(beatmapPath)) {
            return operation.apply(calculator, beatmap);
        }
    }

    private static <T> T withBeatmap(
            AlgorithmVersion algorithm,
            byte[] beatmapBytes,
            BiFunction<RosuPp, Beatmap, T> operation)
    {
        Objects.requireNonNull(algorithm, "algorithm");
        try (RosuPp calculator = RosuPp.forVersion(algorithm);
             Beatmap beatmap = calculator.loadBeatmap(beatmapBytes)) {
            return operation.apply(calculator, beatmap);
        }
    }
}
