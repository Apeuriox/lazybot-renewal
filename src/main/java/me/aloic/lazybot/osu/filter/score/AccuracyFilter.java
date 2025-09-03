package me.aloic.lazybot.osu.filter.score;

import me.aloic.lazybot.enums.FilterOperatorEnum;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.filter.ScoreFilter;

public class AccuracyFilter implements ScoreFilter
{
    private final double threshold;
    private final FilterOperatorEnum operator;

    public AccuracyFilter(double threshold, FilterOperatorEnum operator) {
        this.threshold = threshold;
        this.operator = operator;
    }


    @Override
    public boolean filter(ScoreLazerDTO score)
    {
        return ScoreFilter.numericComparison(operator, score.getAccuracy()*100.0, threshold);
    }
}