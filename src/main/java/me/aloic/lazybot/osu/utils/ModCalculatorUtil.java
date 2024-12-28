package me.aloic.lazybot.osu.utils;


import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ModSetting;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapAttributeVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.enums.OsuMode;

import java.util.List;
import java.util.Optional;

public class ModCalculatorUtil
{

    public static void afterModMapInfo(ScoreVO initialScore)
    {
        BeatmapAttributeVO attributes=new BeatmapAttributeVO(initialScore.getBeatmap().getAr(), initialScore.getBeatmap().getAccuracy(),
                                                            initialScore.getBeatmap().getCs(), initialScore.getBeatmap().getDrain(),
                                                            initialScore.getBeatmap().getBpm(), initialScore.getMode(), initialScore.getBeatmap().getTotal_length());
        initialScore.getBeatmap().setAttributes(calcAllValues(attributes,initialScore.getModJSON(), OsuMode.getMode(initialScore.getMode())));
    }

    private static double getArAfterRateChange(double ar, double clockRate)
    {
        double preempt = ar > 5.0 ? 1200.0 - 750.0 * (ar - 5.0) / 5.0 :
                1200.0 + 600.0 * (5.0 - ar) / 5.0;
        double newAR;
        if (preempt < 150.0) {
            newAR = 12.0;
        } else if (preempt < 1200) {
            newAR = 5.0 + (1200.0 - (preempt / clockRate)) / 150.0;
        } else {
            newAR = 5.0 - (preempt / clockRate) / 120.0 + 10.0;
        }
        return newAR;
    }

    private static double getOdAfterRateChange(double od,double clockRate) {
        double hitWindow_300 = (80.0 - (od * 6.0)) / clockRate;
        return Math.min(((80.0 - hitWindow_300) / 6.0), 12.17);
    }

    private static double getOdAfterRateChangeTaiko(double od,double clockRate) {
        double hitWindow_300 = (50.0 - (od * 3.0)) / clockRate;
        return Math.min(((50.0 - hitWindow_300) / 3.0), 13.83);
    }

    public static BeatmapAttributeVO calcAllValues(BeatmapAttributeVO attributes, List<Mod> mods, OsuMode mode) {
        for(Mod mod: mods) {
            if (mod.getAcronym().equals("DA")) {
                attributes.setAr(mod.getSettings().getApproach_rate());
                attributes.setOd(mod.getSettings().getOverall_Difficulty());
                attributes.setCs(mod.getSettings().getCircle_size());
                attributes.setHp(mod.getSettings().getDrain_rate());
            }
        }
        for(Mod mod: mods) {
            if (mod.getAcronym().equals("HR")) {
                attributes.setAr(attributes.getAr()*1.4>10?10:attributes.getAr()*1.4);
                attributes.setOd(attributes.getOd()*1.4>10?10:attributes.getOd()*1.4);
                attributes.setCs(attributes.getCs()*1.3>10?10:attributes.getCs()*1.3);
                attributes.setHp(attributes.getHp()*1.4>10?10:attributes.getHp()*1.4);
            }
            else if (mod.getAcronym().equals("EZ")) {
                attributes.setAr(attributes.getAr()*0.5<0?0:attributes.getAr()*0.5);
                attributes.setOd(attributes.getOd()*0.5<0?0:attributes.getOd()*0.5);
                attributes.setCs(attributes.getCs()*0.5<0?0:attributes.getCs()*0.5);
                attributes.setHp(attributes.getHp()*0.5<0?0:attributes.getHp()*0.5);
            }
        }
        for(Mod mod: mods)
        {
            if (mod.getAcronym().equals("DT")||mod.getAcronym().equals("NC")) {
                if(mod.getSettings()==null) {
                    mod.setSettings(new ModSetting());
                }
                attributes.setAr(getArAfterRateChange(attributes.getAr(), Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(1.5)));
                if(mode== OsuMode.Osu||mode== OsuMode.Catch) {
                    attributes.setOd(getOdAfterRateChange(attributes.getOd(), Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(1.5)));
                }
                else if(mode== OsuMode.Taiko) {
                    attributes.setOd(getOdAfterRateChangeTaiko(attributes.getOd(), Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(1.5)));
                }
                attributes.setBpm(attributes.getBpm()*Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(1.5));
                attributes.setLength((int) Math.round(attributes.getLength()/Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(1.5)));
            }
            else if(mod.getAcronym().equals("HT")||mod.getAcronym().equals("DC"))
            {
                if(mod.getSettings()==null) {
                    mod.setSettings(new ModSetting());
                }
                attributes.setAr(getArAfterRateChange(attributes.getAr(), Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(0.75)));
                if(mode== OsuMode.Osu||mode== OsuMode.Catch) {
                    attributes.setOd(getOdAfterRateChange(attributes.getOd(), Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(0.75)));
                }
                else if(mode== OsuMode.Taiko) {
                    attributes.setOd(getOdAfterRateChangeTaiko(attributes.getOd(), Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(0.75)));
                }
                attributes.setBpm(attributes.getBpm()*Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(0.75));
                attributes.setLength((int) Math.round(attributes.getLength()/Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(0.75)));
            }
        }
        return attributes;
    }
}
