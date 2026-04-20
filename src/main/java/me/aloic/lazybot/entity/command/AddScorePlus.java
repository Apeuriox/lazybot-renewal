package me.aloic.lazybot.entity.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.dto.plus.LazybotScorePerformance;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;


@Data
@AllArgsConstructor
public class AddScorePlus
{
    private ScoreVO score;
    private LazybotScorePerformance scorePlus;

}
