package me.aloic.lazybot.osu.dao.entity.dto.player;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BeatmapUserScoreLazer implements Serializable
{
    private Integer position;
    private ScoreLazerDTO score;
}