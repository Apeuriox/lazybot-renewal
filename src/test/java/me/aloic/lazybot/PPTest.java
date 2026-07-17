//package me.aloic.lazybot;
//
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson2.JSON;
//import desu.life.RosuFFI;
//import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
//import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ModSetting;
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
//import java.util.ArrayList;
//import java.util.List;
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
//        JniPerformance performance=new JniBeatmap(Files.readAllBytes(Path.of("X:\\5704969.osu"))).createPerformance();
//        List<Mod> hrdt = new ArrayList<>();
//        hrdt.add(new Mod("HR"));
//        hrdt.add(new Mod("DT"));
//
//        performance.setCombo(2646);
//        performance.setN300(2451);
//        performance.setN100(101);
//        performance.setN50(0);
//        performance.setMisses(0);
//        performance.setLargeTick(820);
//        performance.setSliderEnds(400);
//        performance.setMods(JSONUtil.toJsonStr(hrdt),OsuMode.Osu);
//        performance.setLazer(true);
////        System.out.println("original: " + performance.calculate());
//
//
//        List<Mod> hrdtModded = new ArrayList<>();
//        hrdtModded.add(new Mod("DT"));
//        Mod daMod = new Mod("DA", new ModSetting());
//        ModSetting hrdtForCellar = new ModSetting();
//        hrdtForCellar.setCircle_size(4.42);
//        hrdtForCellar.setApproach_rate(8.5);
//        hrdtForCellar.setOverall_difficulty(10.0);
//        daMod.setSettings(hrdtForCellar);
//        hrdtModded.add(daMod);
//
//        performance.setMods(JSONUtil.toJsonStr(hrdt),OsuMode.Osu);
//        System.out.println("AR 10: " + performance.calculate());
//    }
//
//
//    @Test
//    public void testRosuSendanLife() throws IOException
//    {
//        JniPerformance performance=new JniBeatmap(Files.readAllBytes(Path.of("X:\\2267564.osu"))).createPerformance();
//        List<Mod> cl = new ArrayList<>();
//        cl.add(new Mod("CL"));
//        cl.add(new Mod("FL"));
//
//        performance.setCombo(1283);
//        performance.setN300(1108);
//        performance.setN100(19);
//        performance.setN50(0);
////        performance.setLegacyTotalScore(39708520);
//        performance.setLegacyTotalScore(42091031);
//        performance.setMisses(1);
//
////        performance.setMods(JSONUtil.toJsonStr(cl),OsuMode.Osu);
//        performance.setLazer(false);
////        System.out.println("original: " + performance.calculate());
//
//
//        List<Mod> highArMod = new ArrayList<>();
//        highArMod.add(new Mod("CL"));
//        Mod daMod = new Mod("DA", new ModSetting());
//        ModSetting highArModFor = new ModSetting();
//        highArModFor.setApproach_rate(11.0);
//        daMod.setSettings(highArModFor);
//        highArMod.add(daMod);
//
//        List<Mod> hrModList = new ArrayList<>();
//        hrModList.add(new Mod("CL"));
//        hrModList.add(new Mod("HR"));
//
//        performance.setMods(JSONUtil.toJsonStr(cl),OsuMode.Osu);
//        System.out.println("hr: " + performance.calculate());
//    }
//
//}
