package me.aloic.lazybot.osu.filter.score;

import lombok.AllArgsConstructor;
import me.aloic.lazybot.enums.FilterOperatorEnum;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.filter.ScoreFilter;


//disabled due to insufficient data
@AllArgsConstructor
public class MaxComboFilter implements ScoreFilter
{
    private final int threshold;
    private final FilterOperatorEnum operator;


    @Override
    public boolean filter(ScoreLazerDTO score) {
        return ScoreFilter.numericComparison(operator, score.getBeatmap().getMax_combo(), threshold);
    }

}