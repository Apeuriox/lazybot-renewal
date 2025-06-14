package me.aloic.lazybot.osu.utils;

import jakarta.annotation.Nonnull;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.entity.vo.*;
import me.aloic.lazybot.parameter.BpifParameter;
import me.aloic.lazybot.parameter.LazybotCommandParameter;
import me.aloic.lazybot.util.CommonTool;
import me.aloic.lazybot.util.TransformerUtil;
import me.aloic.lazybot.util.VirtualThreadExecutorHolder;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OsuToolsUtil
{

    public static BeatmapVO setupBeatmapVO(BeatmapDTO beatmapDTO)
    {
        BeatmapVO beatmapVO = TransformerUtil.beatmapTransform(beatmapDTO);
        beatmapVO.setBgUrl(AssertDownloadUtil.svgAbsolutePath(beatmapVO.getBeatmapset_id()));
        return beatmapVO;
    }
    public static MapScore setupPlayerStatics(MapScore mapScore, PlayerInfoDTO player)
    {
        String bannerUrl = AssertDownloadUtil.bannerAbsolutePath(player,false);
        String avatarUrl = AssertDownloadUtil.avatarAbsolutePath(player,false);
        mapScore.setAvatarUrl(avatarUrl);
        mapScore.setBannerUrl(bannerUrl);
        return mapScore;
    }
    public static List<MapScore> setupPlayerStatics(List<MapScore> mapScore, PlayerInfoDTO player)
    {
        String bannerUrl = AssertDownloadUtil.bannerAbsolutePath(player,false);
        String avatarUrl = AssertDownloadUtil.avatarAbsolutePath(player,false);
        for (MapScore score : mapScore)
        {
            score.setPlayerName(player.getUsername());
            score.setAvatarUrl(avatarUrl);
            score.setBannerUrl(bannerUrl);
        }
        return mapScore;
    }

    public static ScoreVO setupScoreVO(BeatmapDTO beatmapDTO, ScoreLazerDTO scoreLazerDTO, Boolean override)
    {
        ScoreVO scoreVO = TransformerUtil.transformScoreLazerToScoreVO(scoreLazerDTO);
        scoreVO.setBeatmap(OsuToolsUtil.setupBeatmapVO(beatmapDTO));
        return setupScoreVOLocalCache(override, scoreVO);
    }

    public static ScoreVO setupScoreVOCompact(BeatmapDTO beatmapDTO, ScoreLazerDTO scoreLazerDTO, Boolean override)
    {
        ScoreVO scoreVO = TransformerUtil.transformScoreLazerToScoreVO(scoreLazerDTO);
        BeatmapVO beatmapVO = TransformerUtil.beatmapTransformCompact(beatmapDTO);
        beatmapVO.setBgUrl(AssertDownloadUtil.svgAbsolutePath(beatmapVO.getBeatmapset_id()));
        scoreVO.setBeatmap(beatmapVO);
        return setupScoreVOLocalCache(override, scoreVO);
    }

    @NotNull
    private static ScoreVO setupScoreVOLocalCache(Boolean override, ScoreVO scoreVO)
    {
        try {
            scoreVO.setPpDetailsLocal(RosuUtil.getPPStats(AssertDownloadUtil.beatmapPath(scoreVO,override), scoreVO));
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("Error during recalculations/重算成绩详情时出错: " + e.getMessage());
        }
        if (CommonTool.modsContainsAnyOfStarChanging(scoreVO.getMods()))
            scoreVO.getBeatmap().setDifficult_rating(scoreVO.getPpDetailsLocal().getStar());
        if (scoreVO.getPp() == null)
            scoreVO.setPp(scoreVO.getPpDetailsLocal().getCurrentPP());
        ModCalculatorUtil.afterModMapInfo(scoreVO);
        return scoreVO;
    }


    public static List<ScoreVO> setUpImageStatic(List<ScoreVO> scoreVOList)
    {
        List<CompletableFuture<ScoreVO>> futureList = scoreVOList.stream()
                .map(scoreVO -> CompletableFuture.supplyAsync(() -> {
                    scoreVO.getBeatmap().setBgUrl(AssertDownloadUtil.svgAbsolutePath(scoreVO.getBeatmap().getBeatmapset_id()));
                    try {
                        scoreVO.setPpDetailsLocal(RosuUtil.getPPStats(
                                AssertDownloadUtil.beatmapPath(scoreVO, false), scoreVO));
                        if (scoreVO.getPpDetailsLocal().getStar() != null) {
                            scoreVO.getBeatmap().setDifficult_rating(scoreVO.getPpDetailsLocal().getStar());
                        }
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

    public static List<ScoreSequence> setUpImageStaticSequence(List<ScoreSequence> scoreSequences)
    {
        List<CompletableFuture<ScoreSequence>> futureList = scoreSequences.stream()
                .map(scoreSequence -> CompletableFuture.supplyAsync(() -> {
                    scoreSequence.getBeatmap().setBgUrl(AssertDownloadUtil.svgAbsolutePath(scoreSequence.getBeatmap().getBeatmapset_id()));
                    ModCalculatorUtil.setupBpmChange(scoreSequence);
                    try
                    {
                        scoreSequence.setPpDetails(RosuUtil.getPPStats(AssertDownloadUtil.beatmapPath(scoreSequence.getBeatmap().getBid(), false), scoreSequence));
                        if (scoreSequence.getPpDetails().getStar() != null)
                        {
                            scoreSequence.getBeatmap().setDifficult_rating(scoreSequence.getPpDetails().getStar());
                        }
                    } catch (Exception e)
                    {
                        throw new LazybotRuntimeException("重算成绩详情时出错: " + e.getMessage());
                    }
                    return scoreSequence;
                }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR))
                .toList();

        return futureList.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }


    public static void setupFixedPPStats(ScoreVO scoreVO, boolean conditions)
    {
        if(conditions)
        {
            scoreVO.getBeatmap().setBgUrl(AssertDownloadUtil.svgAbsolutePath(scoreVO.getBeatmap().getBeatmapset_id()));
            try
            {
                scoreVO.setPpDetailsLocal(RosuUtil.getPPStats(AssertDownloadUtil.beatmapPath(scoreVO,false), scoreVO));
                if (scoreVO.getPpDetailsLocal().getStar() != null)
                {
                    scoreVO.getBeatmap().setDifficult_rating(scoreVO.getPpDetailsLocal().getStar());
                    scoreVO.setPp(scoreVO.getPpDetailsLocal().getIfFc());
                }
            } catch (Exception e)
            {
                throw new LazybotRuntimeException("重算成绩详情时出错, 请重试");
            }
        }
    }

    public static PlayerInfoVO setupPlayerInfoVO(PlayerInfoDTO playerInfoDTO)
    {
        playerInfoDTO.setAvatar_url((AssertDownloadUtil.avatarAbsolutePath(playerInfoDTO,false)));
        return TransformerUtil.userTransform(playerInfoDTO);
    }

    public static NoChokeListVO setupNoChokeList(PlayerInfoVO info, List<ScoreVO> scoreList,int type)
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

                        } catch (Exception e) {
                            throw new LazybotRuntimeException("[NoChoke指令] 发现异常ScoreVO对象: " + scoreVO);
                        }
                        return scoreVO;
                    }, VirtualThreadExecutorHolder.VIRTUAL_EXECUTOR)).toList();

        scoreList = futureList.stream()
                .map(CompletableFuture::join)
                .sorted(Comparator.comparing(ScoreVO::getPp).reversed()).collect(Collectors.toList());

        double fixedRawPp=CommonTool.totalPpCalculator(scoreList);
        fixedRawPp+=Math.abs(info.getPerformancePoint()-originalRawPp);
        StringBuilder sb=new StringBuilder(String.valueOf(Math.round(info.getPerformancePoint())));
        sb.append(" -> ").append(Math.round(fixedRawPp))
                .append(" (+")
                .append(Math.round(Math.abs(originalRawPp-fixedRawPp)))
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

    public static List<ScoreVO> setupBpifScoreList(BpifParameter params, List<ScoreLazerDTO> scoreLazerDTOS, PlayerInfoVO info) throws IOException
    {
        List<ScoreVO> scoreList=TransformerUtil.scoreTransformForList(scoreLazerDTOS);
        double originalRawPp=CommonTool.totalPpCalculator(scoreList);
        List<Mod> modEntities = wireModEntities(params.getModList());
        processScoreListConcurrently(scoreList,modEntities,params);
        scoreList=scoreList.stream().sorted(Comparator.comparing(ScoreVO::getPp).reversed()).toList();
        for(int i=0;i<params.getRenderSize();i++) {
            scoreList.get(i).getBeatmap().setBgUrl(AssertDownloadUtil.svgAbsolutePath(scoreList.get(i).getBeatmap().getBeatmapset_id()));
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

    private static List<Mod> wireModEntities(List<String> modStrList) {
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
    public static List<ScoreVO> processScoreListConcurrently(List<ScoreVO> scoreList, List<Mod> modEntities, BpifParameter params) {
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
                    try {
                        scoreVO.setPpDetailsLocal(RosuUtil.getPPStats(AssertDownloadUtil.beatmapPath(scoreVO, false), scoreVO));
                    }
                    catch (IOException e) {
                        throw new LazybotRuntimeException("重算成绩时出错");
                    }
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
}
