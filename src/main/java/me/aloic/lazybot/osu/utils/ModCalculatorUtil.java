package me.aloic.lazybot.osu.utils;


import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ModSetting;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapAttributeVO;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.util.CommonTool;

import java.util.*;

public class ModCalculatorUtil
{

    public static void afterModMapInfo(ScoreVO initialScore)
    {
        BeatmapAttributeVO attributes=new BeatmapAttributeVO(initialScore.getBeatmap().getAr(), initialScore.getBeatmap().getAccuracy(),
                                                            initialScore.getBeatmap().getCs(), initialScore.getBeatmap().getDrain(),
                                                            initialScore.getBeatmap().getBpm(), initialScore.getMode(), initialScore.getBeatmap().getTotal_length());
        initialScore.getBeatmap().setAttributes(calcAllValues(attributes,initialScore.getModJSON(), OsuMode.getMode(initialScore.getMode())));
    }
    public static void afterModMapInfo(ScoreLazerDTO initialScore)
    {
        BeatmapAttributeVO attributes=new BeatmapAttributeVO(initialScore.getBeatmap().getAr(), initialScore.getBeatmap().getAccuracy(),
                initialScore.getBeatmap().getCs(), initialScore.getBeatmap().getDrain(),
                initialScore.getBeatmap().getBpm(), OsuMode.getMode(String.valueOf(initialScore.getRuleset_id())).getDescribe(), initialScore.getBeatmap().getTotal_length());
        initialScore.getBeatmap().setAttributes(calcAllValues(attributes,initialScore.getMods(), OsuMode.getMode(initialScore.getRuleset_id())));
    }

    public static void setupBpmChange(ScoreSequence initialScore)
    {
        setupBpmChange(initialScore.getBeatmap(),initialScore.getModList());
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
            if (mod.getAcronym().equals("DA") && mod.getSettings()!=null) {
                Optional.ofNullable(mod.getSettings().getApproach_rate()).ifPresent(attributes::setAr);
                Optional.ofNullable(mod.getSettings().getOverall_difficulty()).ifPresent(attributes::setOd);
                Optional.ofNullable(mod.getSettings().getCircle_size()).ifPresent(attributes::setCs);
                Optional.ofNullable(mod.getSettings().getDrain_rate()).ifPresent(attributes::setHp);
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
                ensureSettingsInitialized(mod);
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
                ensureSettingsInitialized(mod);
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
    private static void setupBpmChange(BeatmapVO beatmap, List<Mod> mods)
    {
        for(Mod mod:mods)
        {
            if(mod.getAcronym().equals("DT")||mod.getAcronym().equals("NC"))
            {
                ensureSettingsInitialized(mod);
                beatmap.setBpm(beatmap.getBpm() * Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(1.50));
            }
            else if (mod.getAcronym().equals("HT")||mod.getAcronym().equals("DC"))
            {
                ensureSettingsInitialized(mod);
                beatmap.setBpm(beatmap.getBpm() * Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(0.75));
            }
        }
    }
    private static void ensureSettingsInitialized(Mod mod) {
        if (mod.getSettings() == null) {
            mod.setSettings(new ModSetting());
        }
    }

    public static boolean compareMods(List<Mod> modJSON, String modRule) {

        // 1. 提取运算符
        char op = modRule.charAt(0);
        String rulePattern = (op == '=' || op == '~' || op == '!' || op == '^')
                ? modRule.substring(1)
                : modRule;

        // 2. 将 modJSON 转为集合
        Set<String> userMods = new HashSet<>();
        if (!CommonTool.isEmpty(modJSON)) {
            for (Mod m : modJSON) {
                userMods.add(m.getAcronym());
            }
        }


        // 3. 从规则字符串中提取出所有出现的 mod（用来当作“参考集合”）
        Set<String> ruleMods = extractKnownMods(rulePattern);

        // 4. 根据运算符判断
        return switch (op) {
            case '=' -> userMods.equals(ruleMods); // 必须完全一致
            case '~' -> userMods.containsAll(ruleMods); // 必须至少包含规则里的 mod
            case '!' -> Collections.disjoint(userMods, ruleMods); // 不允许出现规则中的任意 mod
            case '^' -> ruleMods.containsAll(userMods); // 用户的 mod 都必须在允许列表内
            default -> userMods.containsAll(ruleMods); // 默认行为：包含匹配
        };
    }

    /**
     * 从字符串中按大写字母对提取mod，例如 HRHD → [HR, HD]
     */
    private static Set<String> extractKnownMods(String pattern) {
        Set<String> mods = new HashSet<>();
        for (int i = 0; i < pattern.length() - 1; i += 2) {
            String mod = pattern.substring(i, Math.min(i + 2, pattern.length()));
            mods.add(mod);
        }
        return mods;
    }
}
