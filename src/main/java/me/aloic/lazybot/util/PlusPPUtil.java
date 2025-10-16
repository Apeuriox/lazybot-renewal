package me.aloic.lazybot.util;

import desu.life.RosuFFI;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlusPPUtil
{
    private static final Logger logger = LoggerFactory.getLogger(PlusPPUtil.class);

    public static PPPlusPerformance calcPPPlusStats(String pathToOsuFile, ScoreVO scoreVO) throws RosuFFI.FFIException
    {
        try(RosuFFI.Beatmap beatmap = new RosuFFI.Beatmap(pathToOsuFile)) {
            return calcPPPlusStats(beatmap,
                    scoreVO.getModJSON().stream()
                            .map(Mod::getAcronym)
                            .collect(Collectors.joining()),
                    scoreVO.getStatistics(),
                    scoreVO.getMaxCombo(),
                    scoreVO.getIsLazer());
        }

    }
    public static PPPlusPerformance calcPPPlusStats(String pathToOsuFile, ScoreLazerDTO score) throws RosuFFI.FFIException
    {
        try(RosuFFI.Beatmap beatmap = new RosuFFI.Beatmap(pathToOsuFile)) {
            return calcPPPlusStats(beatmap,
                    score.getMods().stream()
                            .map(Mod::getAcronym)
                            .collect(Collectors.joining()),
                    score.getStatistics(),
                    score.getMax_combo(),
                    score.getLegacy_total_score() == 0);
        }

    }
    public static PPPlusPerformance calcMaxPPPlusStats(String pathToOsuFile, ScoreVO score) throws RosuFFI.FFIException
    {
        try(RosuFFI.Beatmap beatmap = new RosuFFI.Beatmap(pathToOsuFile)) {
            return calcMaxPPPlusStats(beatmap,
                    score.getModJSON().stream()
                            .map(Mod::getAcronym)
                            .collect(Collectors.joining()),
                    score.getStatistics(),
                    score.getIsLazer());


        }
    }
    private static PPPlusPerformance calcPPPlusStats(RosuFFI.Beatmap beatmap, String modAcronyms, ScoreStatisticsLazer statistics, Integer maxCombo, boolean isLazerScore)
    {
        PPPlusPerformance resultPerformance=new PPPlusPerformance();
        try( RosuFFI.Performance performance  = new RosuFFI.Performance()) {
            performance.setCombo(maxCombo);
            performance.setMode(RosuFFI.Mode.Osu);
            performance.setN300(Optional.ofNullable(statistics.getGreat()).orElse(0));
            performance.setN100(Optional.ofNullable(statistics.getOk()).orElse(0));
            performance.setN50(Optional.ofNullable(statistics.getMeh()).orElse(0));
            performance.setMisses(Optional.ofNullable(statistics.getMiss()).orElse(0));

            performance.setLazer(isLazerScore);
            if(isLazerScore) {
                performance.setLargeTickHits(Optional.ofNullable(statistics.getLarge_tick_hit()).orElse(0));
                performance.setSliderEndHits(Optional.ofNullable(statistics.getSlider_tail_hit()).orElse(0));
            }
            performance.setMods(RosuFFI.Mods.fromAcronyms(modAcronyms,RosuFFI.Mode.Osu));
            RosuFFI.RosuPPLib.PerformanceAttributes calcResult = performance.calculate(beatmap);
            resultPerformance.setPp(calcResult.osu.t.pp);
            resultPerformance.setPpAim(calcResult.osu.t.pp_aim);
            resultPerformance.setPpSpeed(calcResult.osu.t.pp_speed);
            resultPerformance.setPpStamina(calcResult.osu.t.pp_stamina);
            resultPerformance.setPpJumpAim(calcResult.osu.t.pp_jump_aim);
            resultPerformance.setPpFlowAim(calcResult.osu.t.pp_flow_aim);
            resultPerformance.setPpPrecision(calcResult.osu.t.pp_precision);
            resultPerformance.setPpAcc(calcResult.osu.t.pp_acc);
            resultPerformance.setEffectiveMissCount(calcResult.osu.t.effective_miss_count);


            performance.setCombo(19999);
            int countOfMiss = Optional.ofNullable(statistics.getMiss()).orElse(0);
            performance.setN300(Optional.ofNullable(statistics.getGreat()).orElse(0) + countOfMiss);
            performance.setMisses(0);

            RosuFFI.RosuPPLib.PerformanceAttributes iffcResult = performance.calculate(beatmap);
            resultPerformance.setIffc(iffcResult.osu.t.pp);

        }
        catch (RosuFFI.FFIException e) {
            logger.error("error during recalculation pp+: {}", e.getMessage());
            throw new LazybotRuntimeException("重算pp+时出错: " + e.getMessage());
        }
        return resultPerformance;
    }
    private static PPPlusPerformance calcMaxPPPlusStats(RosuFFI.Beatmap beatmap, String modAcronyms, ScoreStatisticsLazer statistics, boolean isLazerScore)
    {
        PPPlusPerformance resultPerformance=new PPPlusPerformance();
        try( RosuFFI.Performance performance  = new RosuFFI.Performance()) {
            performance.setMode(RosuFFI.Mode.Osu);
            performance.setCombo(19999);
            performance.setN300(19999);
            performance.setMisses(0);

            performance.setLazer(isLazerScore);
            if(isLazerScore) {
                performance.setLargeTickHits(9999);
                performance.setSliderEndHits(9999);
            }
            performance.setMods(RosuFFI.Mods.fromAcronyms(modAcronyms,RosuFFI.Mode.Osu));
            RosuFFI.RosuPPLib.PerformanceAttributes calcResult = performance.calculate(beatmap);
            resultPerformance.setPp(calcResult.osu.t.pp);
            resultPerformance.setPpAim(calcResult.osu.t.pp_aim);
            resultPerformance.setPpSpeed(calcResult.osu.t.pp_speed);
            resultPerformance.setPpStamina(calcResult.osu.t.pp_stamina);
            resultPerformance.setPpJumpAim(calcResult.osu.t.pp_jump_aim);
            resultPerformance.setPpFlowAim(calcResult.osu.t.pp_flow_aim);
            resultPerformance.setPpPrecision(calcResult.osu.t.pp_precision);
            resultPerformance.setPpAcc(calcResult.osu.t.pp_acc);
            resultPerformance.setEffectiveMissCount(calcResult.osu.t.effective_miss_count);

        }
        catch (RosuFFI.FFIException e) {
            logger.error("error during recalculation pp+ max stats: {}", e.getMessage());
            throw new LazybotRuntimeException("重算最大pp+参数时出错: " + e.getMessage());
        }
        return resultPerformance;
    }
}
