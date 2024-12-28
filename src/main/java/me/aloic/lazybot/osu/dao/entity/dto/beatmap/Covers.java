package me.aloic.lazybot.osu.dao.entity.dto.beatmap;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
public class Covers implements Serializable
{
    private String  cover;
    private String  cover2x;
    private String card;
    private String card2x;
    private String list;
    private String list2x;
    private String slimcover;
    private String slimcover2x;
}
