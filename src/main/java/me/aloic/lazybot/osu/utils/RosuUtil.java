package me.aloic.lazybot.osu.utils;

import cn.hutool.json.JSONUtil;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;
import me.aloic.lazybot.osu.dao.entity.vo.*;
import me.aloic.lazybot.util.CommonTool;
import org.spring.osu.extended.rosu.*;
import org.spring.osu.OsuMode;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RosuUtil
{
    public static PerformanceVO getPPStats(Path pathToOsuFile, ScoreVO scoreVO) throws IOException {
        return getPPStats(pathToOsuFile, JSONUtil.toJsonStr(scoreVO.getModJSON()) ,scoreVO.getStatistics(),scoreVO.getMode(),scoreVO.getMaxCombo(),scoreVO.getIsLazer());
    }
    public static PerformanceVO getPPStats(Path pathToOsuFile, ScoreLazerDTO score) throws IOException {
        return getPPStats(pathToOsuFile, JSONUtil.toJsonStr(score.getMods()) ,score.getStatistics(),String.valueOf(score.getRuleset_id()),score.getMax_combo(), score.getLegacy_total_score() == 0);
    }
    public static PerformanceVO getCurrentPP(Path pathToOsuFile, ScoreLazerDTO score) throws IOException {
        return getCurrentPP(pathToOsuFile, JSONUtil.toJsonStr(score.getMods()) ,score.getStatistics(),String.valueOf(score.getRuleset_id()),score.getMax_combo(), score.getLegacy_total_score() == 0);
    }
    public static PerformanceVO getPPStats(Path pathToOsuFile, ScoreSequence scoreSequence) throws IOException {
        return getPPStats(pathToOsuFile, JSONUtil.toJsonStr(scoreSequence.getModList()) ,scoreSequence.getStatistics(), String.valueOf(scoreSequence.getRulesetId()),scoreSequence.getMaxCombo(),scoreSequence.getIsLazer());
    }
    public static double recalcPerformance(Path pathToOsuFile, MapScore score) throws IOException
    {
        JniBeatmap beatmap=new JniBeatmap(Files.readAllBytes(pathToOsuFile));
        JniPerformanceAttributes rosuResult=getPPStats(beatmap,JSONUtil.toJsonStr(score.getModJSON()),score.getStatistics(),"osu",score.getMaxCombo(),score.getIsLazer());
        return rosuResult.getPP();
    }
    public static void setupMapScorePerformance(JniBeatmap beatmap, MapScore score)
    {
        JniPerformanceAttributes rosuResult = getPPStats(beatmap, JSONUtil.toJsonStr(score.getModJSON()), score.getStatistics(), "osu", score.getMaxCombo(), score.getIsLazer());
        if (score.getPp()==null) {
            score.setPp(rosuResult.getPP());
        }
        score.setStarRating(rosuResult.getStarRating());
        score.setIffc(getIfFc(beatmap,JSONUtil.toJsonStr(score.getModJSON()),score.getStatistics(),"osu",score.getIsLazer()));
    }

    public static void setupMapScorePerformance(Path pathToOsuFile, MapScore score) throws IOException
    {
        setupMapScorePerformance(new JniBeatmap(Files.readAllBytes(pathToOsuFile)),score);
    }

    public static JniDifficultyAttributes nomodMapStats(Path pathToOsuFile, BeatmapDTO beatmapDTO) throws IOException
    {
        return nomodMapStats(new JniBeatmap(Files.readAllBytes(pathToOsuFile)), String.valueOf(beatmapDTO.getMode_int()));
    }
    public static JniDifficultyAttributes nomodMapStats(Path pathToOsuFile, String mode) throws IOException
    {
        return nomodMapStats(new JniBeatmap(Files.readAllBytes(pathToOsuFile)), mode);
    }
    public static JniDifficultyAttributes nomodMapStats(JniBeatmap beatmap, String mode)
    {
       return fullStatsWithMods(beatmap,null,mode);
    }
    public static JniDifficultyAttributes fullStatsWithMods(JniBeatmap beatmap,String modJSON, String mode)
    {
        JniPerformance performance=beatmap.createPerformance();
        OsuMode osuMode=me.aloic.lazybot.osu.enums.OsuMode.convertMode(String.valueOf(mode));
        performance.setMode(osuMode);
        JniPerformanceAttributes rosuResult;
        performance.setAcc(100.0);
        performance.setLazer(true);
        if (modJSON!=null)
            performance.setMods(modJSON,osuMode);
        rosuResult=performance.calculate();
        return switch (rosuResult) {
            case OsuPerformanceAttributes osu -> osu.getDifficulty();
            case TaikoPerformanceAttributes taiko -> taiko.getDifficulty();
            case ManiaPerformanceAttributes mania -> mania.getDifficulty();
            case CatchPerformanceAttributes catchPerformance -> catchPerformance.getDifficulty();
            default -> null;
        };
    }
    public static JniPerformanceAttributes fullStatsPerformanceWithMods(JniBeatmap beatmap,String modJSON, String mode)
    {
        JniPerformance performance=beatmap.createPerformance();
        OsuMode osuMode=me.aloic.lazybot.osu.enums.OsuMode.convertMode(String.valueOf(mode));
        performance.setMode(osuMode);
        performance.setAcc(100.0);
        performance.setLazer(true);
        if (modJSON!=null)
            performance.setMods(modJSON,osuMode);
        return performance.calculate();
    }


    private static PerformanceVO getPPStats(Path pathToOsuFile, String modJSON, ScoreStatisticsLazer statistics, String mode, Integer maxCombo, boolean isLazerScore) throws IOException
    {
        PerformanceVO resultPerformance=new PerformanceVO();
        JniBeatmap beatmap=new JniBeatmap(Files.readAllBytes(pathToOsuFile));
        JniPerformanceAttributes rosuResult=getPPStats(beatmap,modJSON,statistics,mode,maxCombo,isLazerScore);
        resultPerformance.setStar(rosuResult.getStarRating());
        resultPerformance.setCurrentPP(rosuResult.getPP());
        resultPerformance.setAccPPList(getAccPPList(beatmap,modJSON,mode,isLazerScore));
        resultPerformance.setIfFc(getIfFc(beatmap,modJSON,statistics,mode,isLazerScore));
        resultPerformance.setOriginal(rosuResult);
        List<Double> maxStats=getMaxStatsList(beatmap,modJSON,mode,isLazerScore);
        if(maxStats.isEmpty()||maxStats.size()<4) {
            throw new LazybotRuntimeException("Error when calculating max stats with path of " + pathToOsuFile);
        }
        return setUpMapStatics(rosuResult,resultPerformance,maxStats);
    }

    private static PerformanceVO getCurrentPP(Path pathToOsuFile, String modJSON, ScoreStatisticsLazer statistics, String mode, Integer maxCombo, boolean isLazerScore) throws IOException
    {
        PerformanceVO resultPerformance=new PerformanceVO();
        JniBeatmap beatmap=new JniBeatmap(Files.readAllBytes(pathToOsuFile));
        JniPerformanceAttributes rosuResult=getPPStats(beatmap,modJSON,statistics,mode,maxCombo,isLazerScore);
        resultPerformance.setStar(rosuResult.getStarRating());
        resultPerformance.setCurrentPP(rosuResult.getPP());
        return resultPerformance;
    }

    private static PerformanceVO setUpMapStatics(JniPerformanceAttributes rosuResult, PerformanceVO resultPerformance, List<Double> maxStats)
    {
        resultPerformance.setAimPPMax(maxStats.get(0));
        resultPerformance.setSpdPPMax(maxStats.get(1));
        resultPerformance.setAccPPMax(maxStats.get(2));
        resultPerformance.setFlashlightPP(maxStats.get(3));
        if (rosuResult instanceof OsuPerformanceAttributes osu) {
            resultPerformance.setAimPP(osu.getPpAim());
            resultPerformance.setAccPP(osu.getPpAcc());
            resultPerformance.setSpdPP(osu.getPpSpeed());
            resultPerformance.setFlashlightPP(osu.getPpFlashlight());
        }
        else
        {
            resultPerformance.setAimPP(0.0);
            resultPerformance.setAccPP(0.0);
            resultPerformance.setSpdPP(0.0);
            resultPerformance.setFlashlightPP(0.0);
        }
        return resultPerformance;
    }

    public static void setupBeatmapStatistics(BeatmapStatistics bs) throws IOException
    {
        JniBeatmap beatmap=new JniBeatmap(Files.readAllBytes(AssetDownloadUtil.beatmapPath(bs.getBeatmap().getBid(),false)));
        bs.getBeatmap().setDifficultyAttributes(RosuUtil.nomodMapStats(beatmap, bs.getBeatmap().getMode().getDescribe()));
        bs.getBeatmap().setLengthBonus(CommonTool.lengthBonusCalc(bs.getBeatmap().getCountCircles()+bs.getBeatmap().getCountSliders()+bs.getBeatmap().getCountSpinners()));
        ImaginaryPerformance ip=new ImaginaryPerformance();
        OsuPerformanceAttributes performance = (OsuPerformanceAttributes) fullStatsPerformanceWithMods(beatmap, JSONUtil.toJsonStr(bs.getImaginaryMods()),bs.getMode().getDescribe());
        OsuDifficultyAttributes difficultyAttributes = (OsuDifficultyAttributes) bs.getBeatmap().getDifficultyAttributes();
        Map<Integer,Double> resultAccPpList=new ConcurrentHashMap<>();
        resultAccPpList.put(100,getIfFc(beatmap,JSONUtil.toJsonStr(bs.getImaginaryMods()),bs.getMode().getDescribe(),100.0,true));
        resultAccPpList.put(99,getIfFc(beatmap,JSONUtil.toJsonStr(bs.getImaginaryMods()),bs.getMode().getDescribe(),99.0,true));
        resultAccPpList.put(98,getIfFc(beatmap,JSONUtil.toJsonStr(bs.getImaginaryMods()),bs.getMode().getDescribe(),98.0,true));
        resultAccPpList.put(97,getIfFc(beatmap,JSONUtil.toJsonStr(bs.getImaginaryMods()),bs.getMode().getDescribe(),97.0,true));
        resultAccPpList.put(95,getIfFc(beatmap,JSONUtil.toJsonStr(bs.getImaginaryMods()),bs.getMode().getDescribe(),95.0,true));
        resultAccPpList.put(93,getIfFc(beatmap,JSONUtil.toJsonStr(bs.getImaginaryMods()),bs.getMode().getDescribe(),93.0,true));
        double imaginaryPP= getIfFc(beatmap,JSONUtil.toJsonStr(bs.getImaginaryMods()),bs.getMode().getDescribe(),bs.getPerformance().getImaginaryAccuracy(),true);
        ip.setAccPPList(resultAccPpList);
        ip.setAimPP(performance.getPpAim());
        ip.setAccPP(performance.getPpAcc());
        ip.setSpdPP(performance.getPpSpeed());
        ip.setFlashlightPP(performance.getPpFlashlight());
        ip.setStar(performance.getStarRating());
        ip.setAimStar(difficultyAttributes.getAim());
        ip.setSpeedStar(difficultyAttributes.getSpeed());
        ip.setImaginaryAccuracy(bs.getPerformance().getImaginaryAccuracy());
        ip.setImaginaryPP(imaginaryPP);
        bs.setPerformance(ip);

    }

    private static JniPerformanceAttributes getPPStats(JniBeatmap beatmap, String modJSON, ScoreStatisticsLazer statistics,
                                                      String mode, Integer maxCombo, boolean isLazerScore)
    {
        JniPerformance performance=beatmap.createPerformance();
        if(maxCombo!=0)
            performance.setCombo(maxCombo);
        OsuMode osuMode=me.aloic.lazybot.osu.enums.OsuMode.convertMode(mode);
        performance.setMods(modJSON,osuMode);
        JniPerformanceAttributes rosuResult;
        switch (osuMode)
        {
            case Osu:
                performance.setN300(Optional.ofNullable(statistics.getGreat()).orElse(0));
                performance.setN100(Optional.ofNullable(statistics.getOk()).orElse(0));
                performance.setN50(Optional.ofNullable(statistics.getMeh()).orElse(0));
                if(maxCombo!=0)
                    performance.setMisses(Optional.ofNullable(statistics.getMiss()).orElse(0));
                performance.setLazer(isLazerScore);
                if(isLazerScore) {
                    performance.setLargeTick(Optional.ofNullable(statistics.getLarge_tick_hit()).orElse(0));
                    performance.setSliderEnds(Optional.ofNullable(statistics.getSlider_tail_hit()).orElse(0));
                }
                rosuResult= performance.calculate();
                break;
            case Taiko:
                performance.setN300(Optional.ofNullable(statistics.getGreat()).orElse(0));
                performance.setN100(Optional.ofNullable(statistics.getOk()).orElse(0));
                if(maxCombo!=0)
                    performance.setMisses(Optional.ofNullable(statistics.getMiss()).orElse(0));
                rosuResult= performance.calculate();
                break;
            case Mania:
                performance.setGeki(Optional.ofNullable(statistics.getPerfect()).orElse(0));
                performance.setN300(Optional.ofNullable(statistics.getGreat()).orElse(0));
                performance.setKatu(Optional.ofNullable(statistics.getGood()).orElse(0));
                performance.setN100(Optional.ofNullable(statistics.getOk()).orElse(0));
                performance.setN50(Optional.ofNullable(statistics.getMeh()).orElse(0));
                performance.setMisses(Optional.ofNullable(statistics.getMiss()).orElse(0));
                rosuResult= performance.calculate();
                break;
            case Catch:
                performance.setN300(Optional.ofNullable(statistics.getGreat()).orElse(0));
                performance.setN100(Optional.ofNullable(statistics.getLarge_tick_hit()).orElse(0));
                performance.setN50(Optional.ofNullable(statistics.getSmall_tick_hit()).orElse(0));
                performance.setKatu(Optional.ofNullable(statistics.getSmall_tick_miss()).orElse(0));
                if(maxCombo!=0)
                    performance.setMisses(Optional.ofNullable(statistics.getMiss()).orElse(0));
                rosuResult= performance.calculate();
                break;
            default:
                throw new IllegalStateException("Unsupported mode: " + mode);
        }
        return rosuResult;
    }

    private static double getIfFc(JniBeatmap beatmap, String modJSON, ScoreStatisticsLazer statistics, String mode, boolean isLazerScore)
    {
        return getPPStats(beatmap,modJSON,statistics,mode,0,isLazerScore).getPP();
    }
    private static double getIfFc(JniBeatmap beatmap,String modJSON,String mode,double accuracy,boolean isLazerScore)
    {
        JniPerformance performance = beatmap.createPerformance();
        performance.setMods(modJSON,me.aloic.lazybot.osu.enums.OsuMode.convertMode(mode));
        performance.setAcc(accuracy);
        performance.setLazer(isLazerScore);
        return performance.calculate().getPP();
    }
    private static Map<Integer,Double> getAccPPList(JniBeatmap beatmap, String modJSON, String mode,boolean isLazerScore)
    {
        Map<Integer,Double> result=new ConcurrentHashMap<>();
        result.put(100,getIfFc(beatmap,modJSON,mode,100.0,isLazerScore));
        result.put(99,getIfFc(beatmap,modJSON,mode,99.0,isLazerScore));
        result.put(98,getIfFc(beatmap,modJSON,mode,98.0,isLazerScore));
        result.put(97,getIfFc(beatmap,modJSON,mode,97.0,isLazerScore));
        result.put(95,getIfFc(beatmap,modJSON,mode,95.0,isLazerScore));
        return result;
    }
    private static List<Double> getMaxStatsList(JniBeatmap beatmap, String modJSON, String mode,boolean isLazerScore)
    {
        List<Double> result=new ArrayList<>();
        JniPerformance performance = beatmap.createPerformance();
        performance.setMode(me.aloic.lazybot.osu.enums.OsuMode.convertMode(mode));
        performance.setLazer(isLazerScore);
        performance.setAcc(100.0);
        performance.setMods(modJSON,me.aloic.lazybot.osu.enums.OsuMode.convertMode(mode));
        JniPerformanceAttributes rosuResult=performance.calculate();
        if (rosuResult instanceof OsuPerformanceAttributes) {
            OsuPerformanceAttributes osu=(OsuPerformanceAttributes) rosuResult;
            result.add(osu.getPpAim());
            result.add(osu.getPpSpeed());
            result.add(osu.getPpAcc());
            result.add(osu.getPpFlashlight());
        }
        else {
            result.add(0.0);
            result.add(0.0);
            result.add(0.0);
            result.add(0.0);
        }
        return result;
    }
}
