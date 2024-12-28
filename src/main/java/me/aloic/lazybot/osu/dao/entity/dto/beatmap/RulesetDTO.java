package me.aloic.lazybot.osu.dao.entity.dto.beatmap;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


//MAY NOT BE CORRECT
@Data
@NoArgsConstructor
public class RulesetDTO implements Serializable
{
    private static String fruits="fruits";
    private static String mania="mania";
    private static String osu="osu";
    private static String taiko="taiko";
}
