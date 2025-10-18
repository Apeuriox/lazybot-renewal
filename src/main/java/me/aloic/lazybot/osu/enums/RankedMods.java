package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ModSetting;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum RankedMods
{
    Easy("EZ", null),
    NoFail("NF", null),
    HalfTime("HT", new ModSetting(0.75)),
    DayCore("DC", new ModSetting(0.75)),
    Nightcore("NC", new ModSetting(1.5)),
    DoubleTime("DT", new ModSetting(1.5)),
    HardRock("HR", null),
    Flashlight("FL", null),
    Hidden("HD", null),
    SuddenDeath("SD", null),
    Perfect("PF", null),
    SpunOut("SO", null),
    Blinds("BL",null),
    AccuracyChallenge("AC",null),
    Traceable("TC",null),
    Muted("MU",null),
    NoScope("NS",null);


    private final String acronym;
    //when rate time processing was finished this was needed
    private final ModSetting setting;

    public static boolean checkModsRankability(List<Mod> mods) {
        if (mods == null || mods.isEmpty()) {
            return true;
        }
        Map<String, RankedMods> acronymMap = Arrays.stream(RankedMods.values())
                .collect(Collectors.toMap(RankedMods::getAcronym, Function.identity()));
        for (Mod mod : mods) {
            RankedMods rankedMod = acronymMap.get(mod.getAcronym());
            // temp solution, we should compare it with each mod
            if (rankedMod == null || mod.getSettings() != null) {
                return false;
            }
        }

        return true;
    }
}
