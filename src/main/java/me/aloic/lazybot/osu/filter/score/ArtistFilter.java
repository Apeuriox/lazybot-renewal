package me.aloic.lazybot.osu.filter.score;

import lombok.AllArgsConstructor;
import me.aloic.lazybot.enums.FilterOperatorEnum;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.filter.ScoreFilter;
@AllArgsConstructor
public class ArtistFilter implements ScoreFilter
{
    private final String target;
    private final FilterOperatorEnum operator;


    @Override
    public boolean filter(ScoreLazerDTO score)
    {
        return ScoreFilter.stringComparison(operator, score.getBeatmapset().getArtist(), target);
    }

}