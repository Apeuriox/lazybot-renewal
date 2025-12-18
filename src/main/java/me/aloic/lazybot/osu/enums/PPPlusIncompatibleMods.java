package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ModSetting;

import java.util.List;
@AllArgsConstructor
@Getter
public enum PPPlusIncompatibleMods
{
    HalfTime("HT", new ModSetting(0.75)),
    DayCore("DC", new ModSetting(0.75)),
    Nightcore("NC", new ModSetting(1.5)),
    DoubleTime("DT", new ModSetting(1.5));


    private final String acronym;
    private final ModSetting setting;

    public static boolean checkModsCompatibility(List<Mod> mods) {
        if (mods == null || mods.isEmpty()) {
            return true;
        }
        for (Mod mod : mods) {
          if (mod.getAcronym().equals("DT") || mod.getAcronym().equals("NC") || mod.getAcronym().equals("HT") || mod.getAcronym().equals("DC")) {
             if (mod.getSettings()!=null)
                 if(mod.getSettings().getSpeed_change()!=null)
                     return false;
          }
          if (mod.getAcronym().equals("DA"))
              return false;
        }
        return true;
    }
}
