package me.aloic.lazybot.osu.filter.score;

import lombok.AllArgsConstructor;
import me.aloic.lazybot.enums.FilterOperatorEnum;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapAttributeVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.filter.ScoreFilter;

import java.util.function.Function;

@AllArgsConstructor
public class ApproachingRateFilter implements ScoreFilter
{
    private final double threshold;
    private final FilterOperatorEnum operator;

    @Override
    public boolean filter(ScoreLazerDTO score)
    {
        return ScoreFilter.numericComparison(operator, score.getBeatmap().getAttributes().getAr(), threshold);
    }


}