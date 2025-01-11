package me.aloic.lazybot.osu.dao.entity.dto.player;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class BeatmapUserScores implements Serializable
{
    private List<ScoreLazerDTO> scores;
}