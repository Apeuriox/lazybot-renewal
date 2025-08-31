package me.aloic.lazybot.osu.filter.score;

import lombok.AllArgsConstructor;
import me.aloic.lazybot.enums.FilterOperatorEnum;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.filter.ScoreFilter;

@AllArgsConstructor
public class ModsFilter implements ScoreFilter
{
    private final String target;
    private final FilterOperatorEnum operator;


    @Override
    public boolean filter(ScoreLazerDTO score) {
        if (score.getMods() == null && target.equals("NM")) return true;
        if (score.getMods() == null) return false;
        return ScoreFilter.modsComparison(operator, score.getMods(), target);
    }
}