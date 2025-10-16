package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class PPPlusScore extends ScoreVO
{
    private PPPlusPerformance plusPerformance;
    private PPPlusPerformance maxPerformance;

}
