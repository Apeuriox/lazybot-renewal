package me.aloic.lazybot.osu.dao.entity.dto.player;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreDTO;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BeatmapUserScore implements Serializable
{
    private Integer position;
    private ScoreDTO score;
}
