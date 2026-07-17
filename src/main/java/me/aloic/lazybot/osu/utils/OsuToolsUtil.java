package me.aloic.lazybot.osu.utils;

import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.dto.plus.ScorePerformanceDTO;
import me.aloic.lazybot.osu.dao.entity.dto.starmoon.ScoreStarMoon;
import me.aloic.lazybot.osu.dao.entity.dto.starmoon.UserResponse;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ModSetting;
import me.aloic.lazybot.osu.dao.entity.vo.*;
import me.aloic.lazybot.osu.service.RosuPerformanceService;
import me.aloic.lazybot.parameter.BpifParameter;
import me.aloic.lazybot.util.CommonTool;
import me.aloic.lazybot.util.TransformerUtil;
import me.aloic.lazybot.util.VirtualThreadExecutorHolder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import me.aloic.rosupp.RosuPpException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class OsuToolsUtil
{
    @Resource
    private AssetDownloader assetDownloader;
    @Resource
    private RosuPerformanceService rosuPerformanceService;

    public BeatmapVO setupBeatmapVO(BeatmapDTO beatmapDTO)
    {
        BeatmapVO beatmapVO = TransformerUtil.beatmapTransform(beatmapDTO);
        beatmapVO.setBgUrl(getBeatmapUrl(beatmapVO.getBeatmapset_id()));
        return beatmapVO;
    }
    public BeatmapVO setupBeatmapVO(ScoreStarMoon scoreStarMoon, String mode)
    {
        BeatmapVO beatmapVO = TransformerUtil.beatmapTransform(scoreStarMoon, mode);
        beatmapVO.setBgUrl(getBeatmapUrl(beatmapVO.getBeatmapset_id()));
        return beatmapVO;
    }
    public String getBeatmapUrl(Integer sid)
    {
        return assetDownloader.beatmapBackgroundAbsolutePath(sid);
    }


    public static MapScore setupPlayerStatics(MapScore mapScore, PlayerInfoDTO player)
    {
        if(StringUtils.isNotEmpty(player.getCover_url())) {
            String bannerUrl = AssetDownloadUtil.bannerAbsolutePath(player,false);
            mapScore.setBannerUrl(bannerUrl);
        }
        if(StringUtils.isNotEmpty(player.getAvatar_url())) {
            String avatarUrl = AssetDownloadUtil.avatarAbsolutePath(player,false);
            mapScore.setAvatarUrl(avatarUrl);
        }
        return mapScore;
    }

    public static List<MapScore> setupPlayerStatics(List<MapScore> mapScore, PlayerInfoDTO player)
    {
        String bannerUrl = AssetDownloadUtil.bannerAbsolutePath(player,false);
        String avatarUrl = AssetDownloadUtil.avatarAbsolutePath(player,false);
        for (MapScore score : mapScore)
        {
            score.setPlayerName(player.getUsername());
            score.setAvatarUrl(avatarUrl);
            score.setBannerUrl(bannerUrl);
        }
        return mapScore;
    }

    public ScoreVO setupScoreVO(BeatmapDTO beatmapDTO, ScoreLazerDTO scoreLazerDTO, Boolean override)
    {
        ScoreVO scoreVO = TransformerUtil.transformScoreLazerToScoreVO(scoreLazerDTO);
        scoreVO.setBeatmap(setupBeatmapVO(beatmapDTO));
        return setupScoreVOLocalCache(override, scoreVO);
    }

    public ScoreVO setupScoreVO(ScoreStarMoon scoreStarMoon, UserResponse user, String mode,  Boolean override)
    {
        ScoreVO scoreVO = TransformerUtil.transformScoreStarMoonToScoreVO(scoreStarMoon, user, mode);
        scoreVO.setBeatmap(setupBeatmapVO(scoreStarMoon,mode));
        setBeatmapStarRating(override, scoreVO);
        ModCalculatorUtil.afterModMapInfo(scoreVO);
        return scoreVO;
    }

    public ScoreVO setupScoreVOCompact(BeatmapDTO beatmapDTO, ScoreLazerDTO scoreLazerDTO, Boolean override)
    {
        ScoreVO scoreVO = TransformerUtil.transformScoreLazerToScoreVO(scoreLazerDTO);
        BeatmapVO beatmapVO = TransformerUtil.beatmapTransformCompact(beatmapDTO);
        beatmapVO.setBgUrl(assetDownloader.beatmapBackgroundAbsolutePath(beatmapVO.getBeatmapset_id()));
        scoreVO.setBeatmap(beatmapVO);
        return setupScoreVOLocalCache(override, scoreVO);
    }

    @NotNull
    private ScoreVO setupScoreVOLocalCache(Boolean override, ScoreVO scoreVO)
    {
        try {
            scoreVO.setPpDetailsLocal(rosuPerformanceService.calculatePerformance(AssetDownloadUtil.beatmapPath(scoreVO,override), scoreVO));
        }
        catch (RosuPpException e) {
            throw e;
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("Error during recalculations/重算成绩详情时出错: " + e.getMessage());
        }
        if (CommonTool.modsContainsAnyOfStarChanging(scoreVO.getModJSON()))
            scoreVO.getBeatmap().setDifficult_rating(scoreVO.getPpDetailsLocal().getStar());
        if (scoreVO.getPp() == null)
            scoreVO.setPp(scoreVO.getPpDetailsLocal().getCurrentPP());
        ModCalculatorUtil.afterModMapInfo(scoreVO);
        return scoreVO;
    }

    private void setBeatmapStarRating(Boolean override, ScoreVO scoreVO)
    {
        PerformanceVO performanceVO;
        try {
            performanceVO= rosuPerformanceService.calculatePerformance(AssetDownloadUtil.beatmapPath(scoreVO,override), scoreVO);
        }
        catch (RosuPpException e) {
            throw e;
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("Error during recalculations/重算成绩详情时出错: " + e.getMessage());
        }
        if (CommonTool.modsContainsAnyOfStarChanging(scoreVO.getModJSON()))
            scoreVO.getBeatmap().setDifficult_rating(performanceVO.getStar());
        if (scoreVO.getPp() == null)
            scoreVO.setPp(scoreVO.getPpDetailsLocal().getCurrentPP());
    }


    public List<ScoreVO> setUpImageStatic(List<ScoreVO> scoreVOList)
    {
        List<CompletableFuture<ScoreVO>> futureList = scoreVOList.stream()
                .map(scoreVO -> CompletableFuture.supplyAsync(() -> {
                    scoreVO.getBeatmap().setBgUrl(assetDownloader.beatmapBackgroundAbsolutePath(scoreVO.getBeatmap().getBeatmapset_id()));
                    try {
                        scoreVO.setPpDetailsLocal(rosuPerformanceService.calculatePerformance(
                                AssetDownloadUtil.beatmapPath(scoreVO, false), scoreVO));
                        if (scoreVO.getPpDetailsLocal().getStar() != null) {
                            scoreVO.getBeatmap().setDifficult_rating(scoreVO.getPpDetailsLocal().getStar());
                        }
                    } catch (RosuPpException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new LazybotRuntimeException("重算成绩详情时出错: " + e.getMessage());
                    }
                    return scoreVO;
                }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR))
                .toList();

        return futureList.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<ScoreSequence> setUpImageStaticSequence(List<ScoreSequence> scoreSequences)
    {
        List<CompletableFuture<ScoreSequence>> futureList = scoreSequences.stream()
                .map(scoreSequence -> CompletableFuture.supplyAsync(() -> {
                    scoreSequence.getBeatmap().setBgUrl(assetDownloader.beatmapBackgroundAbsolutePath(scoreSequence.getBeatmap().getBeatmapset_id()));
                    return calcPerformanceForSequence(scoreSequence);
                }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR))
                .toList();

        return futureList.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
    public List<ScoreLazerDTO> setupModStats(List<ScoreLazerDTO> scores)
    {
        List<CompletableFuture<ScoreLazerDTO>> futureList = scores.stream()
                .map(score -> CompletableFuture.supplyAsync(() -> {
                    ModCalculatorUtil.afterModMapInfo(score);
                    if (CommonTool.modsContainsAnyOfStarChanging(score.getMods()))
                    {
                        try {
                           PerformanceVO performance = rosuPerformanceService.calculateCurrentPerformance(AssetDownloadUtil.beatmapPath(score.getBeatmap().getId(), false), score);
                           score.getBeatmap().setDifficulty_rating(performance.getStar());
                        } catch (RosuPpException e) {
                            throw e;
                        } catch (Exception e) {
                            throw new LazybotRuntimeException("重算成绩详情时出错: " + e.getMessage());
                        }
                    }
                    return score;
                }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR))
                .toList();

        return futureList.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
    public List<ScoreSequence> setUpPerformanceSequence(List<ScoreSequence> scoreSequences)
    {
        List<CompletableFuture<ScoreSequence>> futureList = scoreSequences.stream()
                .map(scoreSequence -> CompletableFuture.supplyAsync(() -> {
                    return calcPerformanceForSequence(scoreSequence);
                }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR))
                .toList();

        return futureList.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    @NotNull
    public ScoreSequence calcPerformanceForSequence(ScoreSequence scoreSequence)
    {
        ModCalculatorUtil.setupBpmChange(scoreSequence);
        try
        {
            scoreSequence.setPpDetails(rosuPerformanceService.calculatePerformance(AssetDownloadUtil.beatmapPath(scoreSequence.getBeatmap().getBid(), false), scoreSequence));
            if (scoreSequence.getPpDetails().getStar() != null)
            {
                scoreSequence.getBeatmap().setDifficult_rating(scoreSequence.getPpDetails().getStar());
            }
        } catch (RosuPpException e)
        {
            throw e;
        } catch (Exception e)
        {
            throw new LazybotRuntimeException("重算成绩详情时出错: " + e.getMessage());
        }
        return scoreSequence;
    }


    public void setupFixedPPStats(ScoreVO scoreVO, boolean conditions)
    {
        if(conditions)
        {
            scoreVO.getBeatmap().setBgUrl(assetDownloader.beatmapBackgroundAbsolutePath(scoreVO.getBeatmap().getBeatmapset_id()));
            try
            {
                scoreVO.setPpDetailsLocal(rosuPerformanceService.calculatePerformance(AssetDownloadUtil.beatmapPath(scoreVO,false), scoreVO));
                if (scoreVO.getPpDetailsLocal().getStar() != null)
                {
                    scoreVO.getBeatmap().setDifficult_rating(scoreVO.getPpDetailsLocal().getStar());
                    scoreVO.setPp(scoreVO.getPpDetailsLocal().getIfFc());
                }
            } catch (RosuPpException e)
            {
                throw e;
            } catch (Exception e)
            {
                throw new LazybotRuntimeException("重算成绩详情时出错, 请重试");
            }
        }
    }

    /**
     * Modify score mods to remove reading bonus, then recalculate pp (BpIf pattern).
     * - If DT/NC present: add DA mod with approach_rate = 8.5
     * - If HT/DC present: add DA mod with approach_rate = 10.0
     * - If HD present: remove it entirely
     * - If HR/EZ present: remove them, but embed their OD/CS/HP effects into DA
     * Swaps pp: old pp → ppDetailsLocal.currentPP, new pp → scoreVO.pp.
     * Original mods are restored after recalculation for display.
     */
    public void setupNoReadingPPStats(ScoreVO scoreVO)
    {
        scoreVO.getBeatmap().setBgUrl(assetDownloader.beatmapBackgroundAbsolutePath(scoreVO.getBeatmap().getBeatmapset_id()));
        List<Mod> originalMods = new ArrayList<>(scoreVO.getModJSON());
        List<Mod> modifiedMods = new ArrayList<>(scoreVO.getModJSON());

        boolean hasDT = modifiedMods.stream().anyMatch(m -> "DT".equals(m.getAcronym()) || "NC".equals(m.getAcronym()));
        boolean hasHT = modifiedMods.stream().anyMatch(m -> "HT".equals(m.getAcronym()) || "DC".equals(m.getAcronym()));
        boolean hasHR = modifiedMods.stream().anyMatch(m -> "HR".equals(m.getAcronym()));
        boolean hasEZ = modifiedMods.stream().anyMatch(m -> "EZ".equals(m.getAcronym()));

        // Remove HD mod — no reading bonus without Hidden
        modifiedMods.removeIf(m -> "HD".equals(m.getAcronym()));

        // Resolve or create DA mod (shared across DT/HT/HR/EZ handling)
        Mod daMod = modifiedMods.stream().filter(m -> "DA".equals(m.getAcronym())).findFirst().orElse(null);
        if (daMod != null) {
            if (daMod.getSettings() == null) {
                daMod.setSettings(new ModSetting());
            }
        }

        // Handle HR: embed OD/CS/HP effects into DA, then remove HR
        if (hasHR) {
            if (daMod == null) {
                daMod = new Mod("DA", new ModSetting());
                modifiedMods.add(daMod);
            }
            BeatmapVO beatmap = scoreVO.getBeatmap();
            if (beatmap.getAccuracy() != null)
                daMod.getSettings().setOverall_difficulty(Math.min(beatmap.getAccuracy() * 1.4, 10.0));
            if (beatmap.getCs() != null)
                daMod.getSettings().setCircle_size(Math.min(beatmap.getCs() * 1.3, 10.0));
            if (beatmap.getDrain() != null)
                daMod.getSettings().setDrain_rate(Math.min(beatmap.getDrain() * 1.4, 10.0));
            modifiedMods.removeIf(m -> "HR".equals(m.getAcronym()));
        }

        // Handle EZ: embed OD/CS/HP effects into DA (won't override HR since they're incompatible)
        if (hasEZ) {
            if (daMod == null) {
                daMod = new Mod("DA", new ModSetting());
                modifiedMods.add(daMod);
            }
            BeatmapVO beatmap = scoreVO.getBeatmap();
            if (daMod.getSettings().getOverall_difficulty() == null && beatmap.getAccuracy() != null)
                daMod.getSettings().setOverall_difficulty(Math.max(beatmap.getAccuracy() * 0.5, 0.0));
            if (daMod.getSettings().getCircle_size() == null && beatmap.getCs() != null)
                daMod.getSettings().setCircle_size(Math.max(beatmap.getCs() * 0.5, 0.0));
            if (daMod.getSettings().getDrain_rate() == null && beatmap.getDrain() != null)
                daMod.getSettings().setDrain_rate(Math.max(beatmap.getDrain() * 0.5, 0.0));
            modifiedMods.removeIf(m -> "EZ".equals(m.getAcronym()));
        }

        // Set DA approach_rate for DT/HT, neutralizing AR-based reading bonus
        if (hasDT || hasHT) {
            if (daMod == null) {
                daMod = new Mod("DA", new ModSetting());
                modifiedMods.add(daMod);
            }
            if (hasDT) {
                daMod.getSettings().setApproach_rate(8.5);
            }
            if (hasHT) {
                daMod.getSettings().setApproach_rate(10.0);
            }
        }

        // Apply modified mods for recalculation
        scoreVO.setModJSON(modifiedMods);
        try {
            scoreVO.setPpDetailsLocal(rosuPerformanceService.calculatePerformance(AssetDownloadUtil.beatmapPath(scoreVO, false), scoreVO));
        } catch (RosuPpException e) {
            scoreVO.setModJSON(originalMods);
            throw e;
        } catch (Exception e) {
            scoreVO.setModJSON(originalMods);
            throw new LazybotRuntimeException("重算成绩详情时出错, 请重试");
        }

        // BpIf swap pattern: old pp → currentPP, new pp → scoreVO.pp
        if (scoreVO.getPpDetailsLocal().getStar() != null) {
            scoreVO.getBeatmap().setDifficult_rating(scoreVO.getPpDetailsLocal().getStar());
            double newCurrentPp = scoreVO.getPpDetailsLocal().getCurrentPP();
            scoreVO.getPpDetailsLocal().setCurrentPP(scoreVO.getPp());
            scoreVO.setPp(newCurrentPp);
        }

        // Restore original mods for display
        scoreVO.setModJSON(originalMods);
    }

    public List<ScoreVO> setupNoReadingScoreList(PlayerInfoVO info, List<ScoreVO> scoreList)
    {
        double originalRawPp = CommonTool.totalPpCalculator(scoreList);

        List<CompletableFuture<ScoreVO>> futureList = scoreList.stream()
                .map(scoreVO -> CompletableFuture.supplyAsync(() -> {
                    try {
                        setupNoReadingPPStats(scoreVO);
                    } catch (RosuPpException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new LazybotRuntimeException("[NoReading指令] 发现异常ScoreVO对象: " + scoreVO);
                    }
                    return scoreVO;
                }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR)).toList();

        scoreList = futureList.stream()
                .map(CompletableFuture::join)
                .sorted(Comparator.comparing(ScoreVO::getPp).reversed()).toList();

        double fixedRawPp = CommonTool.totalPpCalculator(scoreList);
        double bonusPp = Math.abs(info.getPerformancePoint() - originalRawPp);
        int difference = (int) Math.round(fixedRawPp - originalRawPp);
        String differenceStr = difference > 0 ? "+" + difference : String.valueOf(difference);
        fixedRawPp += bonusPp;
        StringBuilder sb = new StringBuilder(String.valueOf(Math.round(info.getPerformancePoint())));
        sb.append(" -> ")
                .append(Math.round(fixedRawPp))
                .append(" (")
                .append(differenceStr)
                .append(")");
        info.setFixedPPString(sb.toString());

        return scoreList;
    }

    /**
     * Modify score mods to maximize reading bonus, then recalculate pp (BpIf pattern).
     * - If DT/NC present: add DA with approach_rate = 10
     * - If HT/DC present: add DA with approach_rate = 0
     * - If neither DT/HT nor HR: add DA with approach_rate = 11
     * - If HR present: remove it, embed OD/CS/HP into DA, AR = 11
     * - If EZ present: remove it, embed OD/CS/HP into DA
     * - HD is kept unchanged (HD has its own reading bonus)
     * Swaps pp: old pp → ppDetailsLocal.currentPP, new pp → scoreVO.pp.
     * Original mods are restored after recalculation for display.
     */
    public void setupMaxReadingPPStats(ScoreVO scoreVO)
    {
        scoreVO.getBeatmap().setBgUrl(assetDownloader.beatmapBackgroundAbsolutePath(scoreVO.getBeatmap().getBeatmapset_id()));
        List<Mod> originalMods = new ArrayList<>(scoreVO.getModJSON());
        List<Mod> modifiedMods = new ArrayList<>(scoreVO.getModJSON());

        boolean hasDT = modifiedMods.stream().anyMatch(m -> "DT".equals(m.getAcronym()) || "NC".equals(m.getAcronym()));
        boolean hasHT = modifiedMods.stream().anyMatch(m -> "HT".equals(m.getAcronym()) || "DC".equals(m.getAcronym()));
        boolean hasHR = modifiedMods.stream().anyMatch(m -> "HR".equals(m.getAcronym()));
        boolean hasEZ = modifiedMods.stream().anyMatch(m -> "EZ".equals(m.getAcronym()));

        // HD is kept — Hidden has its own reading bonus, do not remove

        // Resolve or create DA mod
        Mod daMod = modifiedMods.stream().filter(m -> "DA".equals(m.getAcronym())).findFirst().orElse(null);
        if (daMod != null) {
            if (daMod.getSettings() == null) {
                daMod.setSettings(new ModSetting());
            }
        }

        // Handle HR: embed OD/CS/HP effects into DA, then remove HR
        if (hasHR) {
            if (daMod == null) {
                daMod = new Mod("DA", new ModSetting());
                modifiedMods.add(daMod);
            }
            BeatmapVO beatmap = scoreVO.getBeatmap();
            if (beatmap.getAccuracy() != null)
                daMod.getSettings().setOverall_difficulty(Math.min(beatmap.getAccuracy() * 1.4, 10.0));
            if (beatmap.getCs() != null)
                daMod.getSettings().setCircle_size(Math.min(beatmap.getCs() * 1.3, 10.0));
            if (beatmap.getDrain() != null)
                daMod.getSettings().setDrain_rate(Math.min(beatmap.getDrain() * 1.4, 10.0));
            modifiedMods.removeIf(m -> "HR".equals(m.getAcronym()));
        }

        // Handle EZ: embed OD/CS/HP effects into DA
        if (hasEZ) {
            if (daMod == null) {
                daMod = new Mod("DA", new ModSetting());
                modifiedMods.add(daMod);
            }
            BeatmapVO beatmap = scoreVO.getBeatmap();
            if (daMod.getSettings().getOverall_difficulty() == null && beatmap.getAccuracy() != null)
                daMod.getSettings().setOverall_difficulty(Math.max(beatmap.getAccuracy() * 0.5, 0.0));
            if (daMod.getSettings().getCircle_size() == null && beatmap.getCs() != null)
                daMod.getSettings().setCircle_size(Math.max(beatmap.getCs() * 0.5, 0.0));
            if (daMod.getSettings().getDrain_rate() == null && beatmap.getDrain() != null)
                daMod.getSettings().setDrain_rate(Math.max(beatmap.getDrain() * 0.5, 0.0));
            modifiedMods.removeIf(m -> "EZ".equals(m.getAcronym()));
        }

        // Set DA approach_rate to maximize reading bonus
        if (hasDT || hasHT || hasHR || hasEZ) {
            if (daMod == null) {
                daMod = new Mod("DA", new ModSetting());
                modifiedMods.add(daMod);
            }
            if (hasHT) {
                daMod.getSettings().setApproach_rate(0.0);
            } else if (hasDT) {
                daMod.getSettings().setApproach_rate(10.0);
            } else {
                // HR/EZ only, no speed mod — set AR=11 to max reading bonus
                daMod.getSettings().setApproach_rate(11.0);
            }
        } else {
            // No DT/HT/HR/EZ — pure nomod: set AR=11 directly
            if (daMod == null) {
                daMod = new Mod("DA", new ModSetting());
                modifiedMods.add(daMod);
            }
            daMod.getSettings().setApproach_rate(11.0);
        }

        // Apply modified mods for recalculation
        scoreVO.setModJSON(modifiedMods);
        try {
            scoreVO.setPpDetailsLocal(rosuPerformanceService.calculatePerformance(AssetDownloadUtil.beatmapPath(scoreVO, false), scoreVO));
        } catch (RosuPpException e) {
            scoreVO.setModJSON(originalMods);
            throw e;
        } catch (Exception e) {
            scoreVO.setModJSON(originalMods);
            throw new LazybotRuntimeException("重算成绩详情时出错, 请重试");
        }

        // BpIf swap pattern: old pp → currentPP, new pp → scoreVO.pp
        if (scoreVO.getPpDetailsLocal().getStar() != null) {
            scoreVO.getBeatmap().setDifficult_rating(scoreVO.getPpDetailsLocal().getStar());
            double newCurrentPp = scoreVO.getPpDetailsLocal().getCurrentPP();
            scoreVO.getPpDetailsLocal().setCurrentPP(scoreVO.getPp());
            scoreVO.setPp(newCurrentPp);
        }

        // Restore original mods for display (including HD)
        scoreVO.setModJSON(originalMods);
    }

    public List<ScoreVO> setupMaxReadingScoreList(PlayerInfoVO info, List<ScoreVO> scoreList)
    {
        double originalRawPp = CommonTool.totalPpCalculator(scoreList);

        List<CompletableFuture<ScoreVO>> futureList = scoreList.stream()
                .map(scoreVO -> CompletableFuture.supplyAsync(() -> {
                    try {
                        setupMaxReadingPPStats(scoreVO);
                    } catch (RosuPpException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new LazybotRuntimeException("[MaxReading指令] 发现异常ScoreVO对象: " + scoreVO);
                    }
                    return scoreVO;
                }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR)).toList();

        scoreList = futureList.stream()
                .map(CompletableFuture::join)
                .sorted(Comparator.comparing(ScoreVO::getPp).reversed()).toList();

        double fixedRawPp = CommonTool.totalPpCalculator(scoreList);
        double bonusPp = Math.abs(info.getPerformancePoint() - originalRawPp);
        int difference = (int) Math.round(fixedRawPp - originalRawPp);
        String differenceStr = difference > 0 ? "+" + difference : String.valueOf(difference);
        fixedRawPp += bonusPp;
        StringBuilder sb = new StringBuilder(String.valueOf(Math.round(info.getPerformancePoint())));
        sb.append(" -> ")
                .append(Math.round(fixedRawPp))
                .append(" (")
                .append(differenceStr)
                .append(")");
        info.setFixedPPString(sb.toString());

        return scoreList;
    }

    public static PlayerInfoVO setupPlayerInfoVO(PlayerInfoDTO playerInfoDTO)
    {
        playerInfoDTO.setAvatar_url((AssetDownloadUtil.avatarAbsolutePath(playerInfoDTO,false)));
        return TransformerUtil.userTransform(playerInfoDTO);
    }
    public static String getOsuAvatarUrl(PlayerInfoDTO playerInfoDTO)
    {
        return AssetDownloadUtil.avatarAbsolutePath(playerInfoDTO,false);
    }

    public NoChokeListVO setupNoChokeList(PlayerInfoVO info, List<ScoreVO> scoreList, int type)
    {
        NoChokeListVO noChokeListVO=new NoChokeListVO();
        double originalRawPp= CommonTool.totalPpCalculator(scoreList);
        List<CompletableFuture<ScoreVO>> futureList = scoreList.stream()
                    .map(scoreVO -> CompletableFuture.supplyAsync(() -> {
                        try {
                            boolean condition = type == 1
                                    ? !scoreVO.getIsPerfectCombo() && scoreVO.getStatistics().getMiss() <= 1
                                    : !scoreVO.getIsPerfectCombo();
                            setupFixedPPStats(scoreVO, condition);

                        } catch (RosuPpException e) {
                            throw e;
                        } catch (Exception e) {
                            throw new LazybotRuntimeException("[NoChoke指令] 发现异常ScoreVO对象: " + scoreVO);
                        }
                        return scoreVO;
                    }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR)).toList();

        scoreList = futureList.stream()
                .map(CompletableFuture::join)
                .sorted(Comparator.comparing(ScoreVO::getPp).reversed()).toList();

        double fixedRawPp=CommonTool.totalPpCalculator(scoreList);
        double bonusPp=Math.abs(info.getPerformancePoint()-originalRawPp);
        fixedRawPp+=bonusPp;
        StringBuilder sb=new StringBuilder(String.valueOf(Math.round(info.getPerformancePoint())));
        sb.append(" -> ").append(Math.round(fixedRawPp))
                .append(" (+")
                .append(Math.round(Math.abs(originalRawPp-fixedRawPp+bonusPp)))
                .append(")");
        info.setFixedPPString(sb.toString());
        List<ScoreVO> scoreListNeedsFix;
        if(type==1) {
            scoreListNeedsFix = scoreList.stream()
                    .filter(scoreVO -> !scoreVO.getIsPerfectCombo() && scoreVO.getStatistics().getMiss() <= 1 && scoreVO.getPp() - scoreVO.getPpDetailsLocal().getCurrentPP() > 1.5)
                    .collect(Collectors.toList());
        }
        else {
            scoreListNeedsFix = scoreList.stream()
                    .filter(scoreVO -> !scoreVO.getIsPerfectCombo() && scoreVO.getPp() - scoreVO.getPpDetailsLocal().getCurrentPP() > 1.5)
                    .collect(Collectors.toList());
        }
        noChokeListVO.setScoreList(scoreListNeedsFix);
        noChokeListVO.setInfo(info);
        return noChokeListVO;
    }

    public List<ScoreVO> setupBpifScoreList(BpifParameter params, List<ScoreLazerDTO> scoreLazerDTOS, PlayerInfoVO info) throws IOException
    {
        List<ScoreVO> scoreList=TransformerUtil.scoreTransformForList(scoreLazerDTOS);
        double originalRawPp=CommonTool.totalPpCalculator(scoreList);
        List<Mod> modEntities = wireModEntities(params.getModList());
        processScoreListConcurrently(scoreList,modEntities,params);
        scoreList=scoreList.stream().sorted(Comparator.comparing(ScoreVO::getPp).reversed()).toList();
        for(int i=0;i<params.getRenderSize();i++) {
            scoreList.get(i).getBeatmap().setBgUrl(assetDownloader.beatmapBackgroundAbsolutePath(scoreList.get(i).getBeatmap().getBeatmapset_id()));
        }
        double fixedRawPp=CommonTool.totalPpCalculator(scoreList);
        double bonusPp=Math.abs(info.getPerformancePoint()-originalRawPp);
        int difference= (int) Math.round(fixedRawPp-originalRawPp);
        String differenceStr=difference>0?"+"+difference: String.valueOf(difference);
        fixedRawPp+=bonusPp;
        StringBuilder sb=new StringBuilder(String.valueOf(Math.round(info.getPerformancePoint())));
        sb.append(" -> ")
                .append(Math.round(fixedRawPp))
                .append(" (")
                .append(differenceStr)
                .append(")");
        info.setFixedPPString(sb.toString());
        return scoreList;
    }

    public static List<Mod> wireModEntities(List<String> modStrList) {
        List<Mod> modList = new ArrayList<>();
        for (String modStr : modStrList) {
            modStr=modStr.toUpperCase();
            if(modStr.length()!=2) {
                throw new LazybotRuntimeException("Mods invalid: " + modStr);
            }
            Mod mod = new Mod(modStr,null);
            modList.add(mod);
        }
        return modList;
    }


    public List<ScoreVO> processScoreListConcurrently(List<ScoreVO> scoreList, List<Mod> modEntities, BpifParameter params) {
        List<CompletableFuture<ScoreVO>> futures = scoreList.stream()
                .map(scoreVO -> CompletableFuture.supplyAsync(() -> {
                    switch (params.getOperator()) {
                        case "!", "！" -> scoreVO.setModJSON(modEntities.stream().distinct().collect(Collectors.toList()));
                        case "+" -> scoreVO.setModJSON(Stream.concat(scoreVO.getModJSON().stream(), modEntities.stream())
                                .distinct()
                                .collect(Collectors.toList()));
                        case "-" -> scoreVO.getModJSON().removeIf(modEntities::contains);
                        default -> throw new LazybotRuntimeException("Operator invalid: " + params.getOperator());
                    }
                    scoreVO.setPpDetailsLocal(rosuPerformanceService.calculatePerformance(
                            AssetDownloadUtil.beatmapPath(scoreVO, false), scoreVO));
                    if (scoreVO.getPpDetailsLocal().getStar() != null) {
                        scoreVO.getBeatmap().setDifficult_rating(scoreVO.getPpDetailsLocal().getStar());

                        double currentPp = scoreVO.getPpDetailsLocal().getCurrentPP();
                        scoreVO.getPpDetailsLocal().setCurrentPP(scoreVO.getPp());
                        scoreVO.setPp(currentPp);
                    }
                    return scoreVO;
                }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<ScorePerformanceDTO> setupScorePerformanceList(List<ScorePerformanceDTO> scorelist) {
        for (ScorePerformanceDTO score : scorelist) {
            score.setRank(GradeCalculator.calculateGrade(score.getStatistics(),score.getMods()));
            score.getBeatmap().setBgUrl(assetDownloader.beatmapBackgroundAbsolutePath(Math.toIntExact(score.getBeatmap().getSid())));

        }
        return scorelist;
    }

}
