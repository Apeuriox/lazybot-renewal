package me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.enums.OsuMod;


import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mod implements Serializable
{
    private String acronym;
    private ModSetting settings;

    public Mod(String acronym)
    {
        this.acronym=acronym;
    }
    public Mod(OsuMod mod)
    {
        this.acronym=mod.getAcronym();
    }



}
