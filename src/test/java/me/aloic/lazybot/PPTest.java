//package me.aloic.lazybot;
//
//import desu.life.RosuFFI;
//import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;
//import me.aloic.lazybot.osu.dao.entity.vo.PPPlusPerformance;
//import org.junit.jupiter.api.Test;
//import org.spring.osu.OsuMode;
//import org.spring.osu.extended.rosu.JniBeatmap;
//import org.spring.osu.extended.rosu.JniPerformance;
//import org.spring.osu.extended.rosu.JniPerformanceAttributes;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.Optional;
//
//public class PPTest
//{
////    @Test
//    public void testSingleCalc() throws RosuFFI.FFIException
//    {
//        PPPlusPerformance resultPerformance=new PPPlusPerformance();
//        try(RosuFFI.Beatmap beatmap = new RosuFFI.Beatmap("X:\\OSUFILETEST.osu"))
//        {
//            try (RosuFFI.Performance performance = new RosuFFI.Performance())
//            {
//                performance.setCombo(19999);
//                performance.setMode(RosuFFI.Mode.Osu);
//                performance.setN300(19999);
//
//                performance.setLazer(true);
//
//                RosuFFI.RosuPPLib.PerformanceAttributes calcResult = performance.calculate(beatmap);
//                resultPerformance.setPp(calcResult.osu.t.pp);
//                resultPerformance.setPpAim(calcResult.osu.t.pp_aim);
//                resultPerformance.setPpSpeed(calcResult.osu.t.pp_speed);
//                resultPerformance.setPpStamina(calcResult.osu.t.pp_stamina);
//                resultPerformance.setPpJumpAim(calcResult.osu.t.pp_jump_aim);
//                resultPerformance.setPpFlowAim(calcResult.osu.t.pp_flow_aim);
//                resultPerformance.setPpPrecision(calcResult.osu.t.pp_precision);
//                resultPerformance.setPpAcc(calcResult.osu.t.pp_acc);
//                resultPerformance.setEffectiveMissCount(calcResult.osu.t.effective_miss_count);
//                System.out.println(resultPerformance);
//            } catch (RosuFFI.FFIException e)
//            {
//                throw new RuntimeException("重算pp+时出错: " + e.getMessage());
//            }
//        }
//        catch (Exception e)
//        {
//            throw e;
//        }
//    }
//
//    @Test
//    public void testRosu() throws IOException
//    {
//        JniPerformance performance=new JniBeatmap(Files.readAllBytes(Path.of("X:\\4276778.osu"))).createPerformance();
//        performance.setCombo(5);
//        performance.setMode(OsuMode.Osu);
//
//        performance.setN300(5);
//        performance.setN100(1);
//        performance.setN50(0);
//        performance.setMisses(1);
//        performance.setLargeTick(1);
//        performance.setSliderEnds(1);
//        performance.setLazer(true);
//        System.out.println(performance.calculate());
//    }
//
//}
