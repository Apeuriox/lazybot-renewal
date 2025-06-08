package me.aloic.lazybot.osu.utils;

import cn.hutool.json.JSONUtil;
import desu.life.RosuFFI;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;

import java.util.Optional;

public class PlusPPUtil
{
    public static PPPlusPerformance calcPPPlusStats(String pathToOsuFile, ScoreVO scoreVO) throws RosuFFI.FFIException
    {
        try(RosuFFI.Beatmap beatmap = new RosuFFI.Beatmap(pathToOsuFile)) {
            return calcPPPlusStats(beatmap,
                    JSONUtil.toJsonStr(scoreVO.getModJSON()),
                    scoreVO.getStatistics(),
                    scoreVO.getMaxCombo(),
                    scoreVO.getIsLazer());
        }
    }
    private static PPPlusPerformance calcPPPlusStats(RosuFFI.Beatmap beatmap, String modJSON, ScoreStatisticsLazer statistics, Integer maxCombo, boolean isLazerScore)
    {
        PPPlusPerformance resultPerformance=new PPPlusPerformance();
        try( RosuFFI.Performance performance  = new RosuFFI.Performance()) {
            if(maxCombo!=0)
                performance.setCombo(maxCombo);
            performance.setMode(RosuFFI.Mode.Osu);
            performance.setN300(Optional.ofNullable(statistics.getGreat()).orElse(0));
            performance.setN100(Optional.ofNullable(statistics.getOk()).orElse(0));
            performance.setN50(Optional.ofNullable(statistics.getMeh()).orElse(0));
            if(maxCombo!=0)
                performance.setMisses(Optional.ofNullable(statistics.getMiss()).orElse(0));
            performance.setLazer(isLazerScore);
            if(isLazerScore) {
                performance.setLargeTickHits(Optional.ofNullable(statistics.getLarge_tick_hit()).orElse(0));
                performance.setSliderEndHits(Optional.ofNullable(statistics.getSlider_tail_hit()).orElse(0));
            }
            performance.setMods(modJSON);
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
            throw new LazybotRuntimeException("计算pp+时出错: " + e.getMessage());
        }
        return resultPerformance;
    }
}
