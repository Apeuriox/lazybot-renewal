package me.aloic.lazybot.osu.filter.score;

import lombok.AllArgsConstructor;
import me.aloic.lazybot.enums.FilterOperatorEnum;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.filter.ScoreFilter;

import java.util.Optional;

@AllArgsConstructor
public class OKFilter implements ScoreFilter
{
    private final int threshold;
    private final FilterOperatorEnum operator;

    @Override
    public boolean filter(ScoreLazerDTO score) {
        return ScoreFilter.numericComparison(operator, Optional.ofNullable(score.getStatistics().getOk()).orElse(0), threshold);
    }

}