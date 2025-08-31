package me.aloic.lazybot.osu.filter.score;

import lombok.AllArgsConstructor;
import me.aloic.lazybot.enums.FilterOperatorEnum;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.filter.ScoreFilter;

import java.util.Optional;

@AllArgsConstructor
public class RankFilter implements ScoreFilter
{
    private final String target;
    private final FilterOperatorEnum operator;


    @Override
    public boolean filter(ScoreLazerDTO score)
    {
        return ScoreFilter.stringComparison(operator, Optional.ofNullable(score.getRank()).orElse("F"), target);
    }

}