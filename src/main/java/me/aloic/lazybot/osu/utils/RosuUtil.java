package me.aloic.lazybot.osu.utils;

import cn.hutool.json.JSONUtil;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;
import me.aloic.lazybot.osu.dao.entity.vo.PerformanceVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import org.spring.osu.OsuMode;
import org.spring.osu.extended.rosu.JniBeatmap;
import org.spring.osu.extended.rosu.JniPerformance;
import org.spring.osu.extended.rosu.JniPerformanceAttributes;
import org.spring.osu.extended.rosu.OsuPerformanceAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RosuUtil
{
    public static PerformanceVO getPPStats(Path pathToOsuFile, ScoreVO scoreVO) throws IOException {
        return getPPStats(pathToOsuFile, JSONUtil.toJsonStr(scoreVO.getModJSON()) ,scoreVO.getStatistics(),scoreVO.getMode(),scoreVO.getMaxCombo(),scoreVO.getIsLazer());
    }
    public static PerformanceVO getPPStats(Path pathToOsuFile, ScoreSequence scoreSequence) throws IOException {
        return getPPStats(pathToOsuFile, JSONUtil.toJsonStr(scoreSequence.getModList()) ,scoreSequence.getStatistics(), String.valueOf(scoreSequence.getRulesetId()),scoreSequence.getMaxCombo(),scoreSequence.getIsLazer());
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
        List<Double> maxStats=getMaxStatsList(beatmap,modJSON,mode,isLazerScore);
        if(maxStats.isEmpty()||maxStats.size()<4) {
            throw new RuntimeException("Error when calculating max stats with path of " + pathToOsuFile);
        }
        return setUpMapStatics(rosuResult,resultPerformance,maxStats);
    }

    private static PerformanceVO setUpMapStatics(JniPerformanceAttributes rosuResult, PerformanceVO resultPerformance, List<Double> maxStats)
    {
        resultPerformance.setAimPPMax(maxStats.get(0));
        resultPerformance.setSpdPPMax(maxStats.get(1));
        resultPerformance.setAccPPMax(maxStats.get(2));
        resultPerformance.setFlashlightPP(maxStats.get(3));
        if (rosuResult instanceof OsuPerformanceAttributes) {
            OsuPerformanceAttributes osu=(OsuPerformanceAttributes) rosuResult;
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
                performance.setLargeTick(Optional.ofNullable(statistics.getLarge_tick_hit()).orElse(0));
                if(maxCombo!=0)
                    performance.setMisses(Optional.ofNullable(statistics.getSmall_bonus()).orElse(0));
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
