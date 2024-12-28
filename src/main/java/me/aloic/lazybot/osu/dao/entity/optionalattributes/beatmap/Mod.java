package me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


import java.io.Serializable;

@Data
@AllArgsConstructor
public class Mod implements Serializable
{
    @Getter
    private String acronym;
    private ModSetting settings;

}
