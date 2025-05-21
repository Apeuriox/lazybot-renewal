package me.aloic.lazybot.osu.dao.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModWikiPO implements Serializable
{
    private String name;
    private String icon;
    private String abbreviation;
    private List<Integer> compatible_mode;
    private List<Integer> unranked_mode;
    private Integer ranked;
    private String type;
    private List<ModMultiplier> score_multiplier;
    private String default_key;
    private List<ModEffect> effect;
    private List<ModGameplay> gameplay;
    private List<ModIncompatible> incompatible_mod;
    private String additional_info;

}
