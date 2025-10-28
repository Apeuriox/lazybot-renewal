package me.aloic.lazybot.entity.command;

import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.vo.*;

import java.util.List;


@Data
public class PlayerScoreList
{
    private List<ScoreVO> scoreVOList;
    private PlayerInfoVO info;
    private List<ScoreSequence> scoreSequences;

    public PlayerScoreList(List<ScoreVO> scoreVOList, PlayerInfoVO info)
    {
        this.scoreVOList = scoreVOList;
        this.info = info;
    }
    public PlayerScoreList(PlayerInfoVO info, List<ScoreSequence> scoreSequences)
    {
        this.scoreSequences = scoreSequences;
        this.info = info;
    }

}
