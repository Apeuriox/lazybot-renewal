package me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mod implements Serializable
{
    private String acronym;
    private ModSetting settings;
}
