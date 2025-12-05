package me.aloic.lazybot.entity.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotScorePerformance;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;


@Data
@AllArgsConstructor
public class AddScorePlus
{
    private ScoreVO score;
    private LazybotScorePerformance scorePlus;

}
