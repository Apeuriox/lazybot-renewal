package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aloic.lazybot.osu.dao.entity.dto.plus.ScorePerformanceDTO;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PlusScorePerformance extends ScoreVO
{
    private String name;
    private String avatarUrl;
    private int offset;
    private String dimension;
    private List<ScorePerformanceDTO> scores;

    public PlusScorePerformance(List<ScorePerformanceDTO> scores)
    {
        this.scores=scores;
    }

    @Override
    public String toString()
    {
        return "PlusScorePerformance{" +
                "name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", offset=" + offset +
                ", scores=" + scores +
                '}';
    }
}
